package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.exchange.serviceconfiguration.*;
import edu.rit.csh.pings.external.ExternalDispatchService;
import edu.rit.csh.pings.managers.ServiceConfigurationManager;
import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ServiceConfigurationController {

    private final Log log = LogFactory.getLog("pings.service_configuration_controller");

    private final ServiceConfigurationManager serviceConfigurationManager;
    private final ExternalDispatchService externalDispatchService;

    private ServiceInfo fromService(Class<? extends ServiceConfiguration> service) {
        final ServiceInfo ret = new ServiceInfo();
        final ServiceDescription desc = service.getAnnotation(ServiceDescription.class);
        ret.setName(desc.name());
        ret.setDescription(desc.description());
        return ret;
    }

    @GetMapping("/api/service/")
    private List<ServiceInfo> getServices() {
        this.log.info("GET /api/service");
        return this.serviceConfigurationManager.getServices().stream().map(this::fromService).toList();
    }

    @GetMapping("/api/service/{name}/")
    private ServiceInfo getService(@PathVariable String name) {
        this.log.info("GET /api/service/{name}");
        return this.fromService(this.serviceConfigurationManager.getServiceByName(name).orElseThrow());
    }

    @GetMapping("/api/service/{name}/properties")
    private List<ServiceConfigurationProperty> getServiceProperties(@PathVariable String name) {
        this.log.info("GET /api/service/{name}/properties");
        final Class<? extends ServiceConfiguration> service = this.serviceConfigurationManager.getServiceByName(name).orElseThrow();
        List<ServiceConfigurationProperty> props = new ArrayList<>();
        for (Field property : service.getDeclaredFields()) {
            if (property.isAnnotationPresent(ConfigurableProperty.class)) {
                final ConfigurableProperty cprop = property.getAnnotation(ConfigurableProperty.class);
                ServiceConfigurationProperty prop = new ServiceConfigurationProperty();
                prop.setName(cprop.name());
                prop.setDescription(cprop.description());
                prop.setType(cprop.type().getHtmlInputType());
                prop.setEnumValues(Arrays.asList(cprop.enumValues()));
                props.add(prop);
            }
        }
        return props;
    }

    @PostMapping("/api/service-configuration/")
    private void createServiceConfiguration(@AuthenticationPrincipal CSHUser user, @RequestBody ServiceConfigurationCreate create) {
        this.log.info("POST /api/service-configuration");
        final Class<? extends ServiceConfiguration> service = this.serviceConfigurationManager.getServiceByName(create.getName()).orElseThrow();
        this.serviceConfigurationManager.checkDuplicateConfigurations(user.getUsername(), service);
        ServiceConfiguration config;
        try {
            config = service.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            this.log.error("Exception while constructing Service Configuration " + create.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid Service Configuration class file. Please fix code", e);
        } catch (InvocationTargetException e) {
            this.log.error("Exception inside Service Configuration constructor: " + create.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception while creating Service Configuration", e);
        }
        config.setUuid(UUID.randomUUID());
        config.setVerified(false);
        config.setUserRegistrations(Set.of());
        config.setVerificationRequests(new HashSet<>());
        config.setUsername(user.getUsername());
        config.create(create.getProperties());
        config.setDescription(create.getDescription());
        this.serviceConfigurationManager.save(config);
        this.log.debug("Created new Service Configuration. Type: " + create.getName() + ", UUID: " + config.getUuid());
        this.externalDispatchService.getExternalService(config).sendVerification(config);
        this.log.debug("Sent verification code");
    }

    @DeleteMapping("/api/service-configuration/{uuid}")
    private void deleteServiceConfiguration(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("DELETE /api/service-configuration/{uuid}");
        final ServiceConfiguration config = this.serviceConfigurationManager
                .findByUUID(uuid)
                .filter(n -> n.getUsername().equalsIgnoreCase(user.getUsername()))
                .orElseThrow();
        this.serviceConfigurationManager.delete(config);
        this.log.debug("Deleted Service Configuration " + uuid);
    }

    public ServiceConfigurationInfo fromServiceConfiguration(ServiceConfiguration config) {
        ServiceConfigurationInfo ret = new ServiceConfigurationInfo();
        BeanUtils.copyProperties(config, ret);
        ret.setService(this.fromService(config.getClass()));
        List<ConfigurablePropertyInfo> props = new ArrayList<>();
        for (Field field : config.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigurableProperty.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                final ConfigurableProperty cprop = field.getAnnotation(ConfigurableProperty.class);
                ConfigurablePropertyInfo cpi = new ConfigurablePropertyInfo(cprop.name(), cprop.description(), field.get(config).toString(), cprop.type());
                props.add(cpi);
            } catch (IllegalAccessException e) {
                this.log.error("Exception while accessing props field in service configuration", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid Service Configuration class file. Please fix code", e);
            }
        }
        ret.setProperties(props);
        return ret;
    }

    @GetMapping("/api/service-configuration/{uuid}")
    private ServiceConfigurationInfo getServiceConfiguration(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/service-configuration/{uuid}");
        final ServiceConfiguration config = this.serviceConfigurationManager
                .findByUUID(uuid)
                .filter(n -> n.getUsername().equalsIgnoreCase(user.getUsername()))
                .orElseThrow();
        return this.fromServiceConfiguration(config);
    }

    @GetMapping("/api/service-configuration/")
    public List<ServiceConfigurationInfo> getServiceConfigurations(@AuthenticationPrincipal CSHUser user) {
        this.log.info("GET /api/service-configuration");
        return this.serviceConfigurationManager.getByUsername(user.getUsername()).stream().map(this::fromServiceConfiguration).toList();
    }
}
