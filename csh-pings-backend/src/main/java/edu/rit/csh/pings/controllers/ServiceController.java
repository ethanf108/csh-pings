package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.exchange.serviceconfiguration.ServiceConfigurationProperty;
import edu.rit.csh.pings.exchange.serviceconfiguration.ServiceInfo;
import edu.rit.csh.pings.managers.ServiceConfigurationManager;
import edu.rit.csh.pings.managers.ServiceManager;
import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ServiceController {

    private final Log log = LogFactory.getLog("pings.service_controller");

    private final ServiceManager serviceManager;
    private final ServiceConfigurationManager serviceConfigurationManager;

    static ServiceInfo fromService(ServiceDescription desc) {
        final ServiceInfo ret = new ServiceInfo();
        ret.setId(desc.id());
        ret.setName(desc.name());
        ret.setDescription(desc.description());
        return ret;
    }

    @GetMapping("/api/service/")
    private List<ServiceInfo> getServices(
            @AuthenticationPrincipal CSHUser user,
            @RequestParam(defaultValue = "false") boolean onlyCreatable) {
        this.log.info("GET /api/service/?onlyCreatable=" + onlyCreatable);
        Stream<ServiceDescription> stream = this.serviceManager.getServices().stream().map(ServiceManager::getInfo);
        if (onlyCreatable) {
            final List<ServiceDescription> configurations = this.serviceConfigurationManager
                    .getByUsername(user.getUsername())
                    .stream()
                    .map(ServiceConfiguration::getInfo)
                    .distinct()
                    .toList();
            stream = stream.filter(n -> n.allowMultiple() || !configurations.contains(n));
        }
        return stream.map(ServiceController::fromService).toList();
    }

    @GetMapping("/api/service/{id}/")
    private ServiceInfo getService(@PathVariable String id) {
        this.log.info("GET /api/service/" + id);
        return fromService(this.serviceManager.getServiceById(id).map(ServiceManager::getInfo).orElseThrow());
    }

    @GetMapping("/api/service/{id}/properties")
    private List<ServiceConfigurationProperty> getServiceProperties(@PathVariable String id) {
        this.log.info("GET /api/service/" + id + "/properties");
        final Class<? extends ServiceConfiguration> service = this.serviceManager.getServiceById(id).orElseThrow();
        List<ServiceConfigurationProperty> props = new ArrayList<>();
        for (Field property : service.getDeclaredFields()) {
            if (property.isAnnotationPresent(ConfigurableProperty.class)) {
                final ConfigurableProperty cprop = property.getAnnotation(ConfigurableProperty.class);
                ServiceConfigurationProperty prop = new ServiceConfigurationProperty();
                prop.setId(cprop.id());
                prop.setName(cprop.name());
                prop.setDescription(cprop.description());
                prop.setType(cprop.type().getHtmlInputType());
                prop.setEnumValues(Arrays.asList(cprop.enumValues()));
                prop.setValidationRegex(cprop.validationRegex());
                props.add(prop);
            }
        }
        return props;
    }
}
