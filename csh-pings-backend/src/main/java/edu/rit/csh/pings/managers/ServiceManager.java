package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.ServiceMarker;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceManager {

    private final Map<String, Class<? extends ServiceConfiguration>> serviceCache = new HashMap<>();

    private void populateServiceConfigurationCache() {
        if (!this.serviceCache.isEmpty()) {
            return;
        }
        for (Class<?> clazz : ServiceMarker.class.getPermittedSubclasses()) {
            if (!clazz.isAnnotationPresent(ServiceDescription.class)) {
                continue;
            }
            this.serviceCache.put(clazz.getAnnotation(ServiceDescription.class).id(), clazz.asSubclass(ServiceConfiguration.class));
        }
    }

    public List<Class<? extends ServiceConfiguration>> getServices() {
        this.populateServiceConfigurationCache();
        return new ArrayList<>(this.serviceCache.values());
    }

    public Optional<Class<? extends ServiceConfiguration>> getServiceById(String serviceId) {
        this.populateServiceConfigurationCache();
        return Optional.ofNullable(this.serviceCache.get(serviceId));
    }
}
