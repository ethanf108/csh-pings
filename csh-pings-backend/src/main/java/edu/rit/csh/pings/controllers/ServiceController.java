package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.exchange.serviceconfiguration.ServiceConfigurationProperty;
import edu.rit.csh.pings.exchange.serviceconfiguration.ServiceInfo;
import edu.rit.csh.pings.managers.ServiceManager;
import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ServiceController {

    private final Log log = LogFactory.getLog("pings.service_controller");

    private final ServiceManager serviceManager;

    static ServiceInfo fromService(Class<? extends ServiceConfiguration> service) {
        final ServiceInfo ret = new ServiceInfo();
        final ServiceDescription desc = service.getAnnotation(ServiceDescription.class);
        ret.setId(desc.id());
        ret.setName(desc.name());
        ret.setDescription(desc.description());
        return ret;
    }

    @GetMapping("/api/service/")
    private List<ServiceInfo> getServices() {
        this.log.info("GET /api/service");
        return this.serviceManager.getServices().stream().map(ServiceController::fromService).toList();
    }

    @GetMapping("/api/service/{id}/")
    private ServiceInfo getService(@PathVariable String id) {
        this.log.info("GET /api/service/" + id);
        return fromService(this.serviceManager.getServiceById(id).orElseThrow());
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
                props.add(prop);
            }
        }
        return props;
    }
}
