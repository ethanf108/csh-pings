package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.ServiceConfigurationMarker;
import edu.rit.csh.pings.repos.ServiceConfigurationRepo;
import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceConfigurationManager {

    private final ServiceConfigurationRepo serviceConfigurationRepo;
    private final Map<String, Class<? extends ServiceConfiguration>> serviceConfigurationCache = new HashMap<>();

    private void populateServiceConfigurationCache() {
        if (!this.serviceConfigurationCache.isEmpty()) {
            return;
        }
        for (Class<?> clazz : ServiceConfigurationMarker.class.getPermittedSubclasses()) {
            if (!clazz.isAnnotationPresent(ConfigurableProperty.class)) {
                continue;
            }
            this.serviceConfigurationCache.put(clazz.getAnnotation(ConfigurableProperty.class).name(), clazz.asSubclass(ServiceConfiguration.class));
        }
    }

    public <T extends ServiceConfiguration> void save(T t) {
        this.serviceConfigurationRepo.save(t);
    }

    public Optional<? extends ServiceConfiguration> findByUUID(UUID uuid) {
        return Optional.ofNullable(this.serviceConfigurationRepo.findByUuid(uuid));
    }

    public List<Class<? extends ServiceConfiguration>> getServices() {
        this.populateServiceConfigurationCache();
        return new ArrayList<>(this.serviceConfigurationCache.values());
    }

    public Class<? extends ServiceConfiguration> getServiceByName(String name) {
        this.populateServiceConfigurationCache();
        return this.serviceConfigurationCache.get(name);
    }

    public void checkDuplicateConfigurations(String username, Class<? extends ServiceConfiguration> service) {
        if (!service.getAnnotation(ConfigurableProperty.class).allowMultiple() &&
                this.serviceConfigurationRepo.findAllByUsernameIgnoreCase(username)
                        .stream()
                        .map(Object::getClass)
                        .anyMatch(n -> n == service)) {
            throw new IllegalArgumentException("Cannot duplicate service configuration " + service.getAnnotation(ConfigurableProperty.class).name());
        }
    }

    public void delete(ServiceConfiguration config) {
        this.serviceConfigurationRepo.delete(config);
    }

    public List<ServiceConfiguration> getByUsername(String username) {
        return this.serviceConfigurationRepo.findAllByUsernameIgnoreCase(username);
    }
}
