package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.ServiceMarker;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.entities.WebNotificationConfiguration;
import edu.rit.csh.pings.repos.ServiceConfigurationRepo;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceConfigurationManager {

    private final ServiceConfigurationRepo serviceConfigurationRepo;
    //    private final UserRegistrationManager userRegistrationManager;
    private final Map<String, Class<? extends ServiceConfiguration>> serviceCache = new HashMap<>();

    private void populateServiceConfigurationCache() {
        if (!this.serviceCache.isEmpty()) {
            return;
        }
        for (Class<?> clazz : ServiceMarker.class.getPermittedSubclasses()) {
            if (!clazz.isAnnotationPresent(ServiceDescription.class)) {
                continue;
            }
            this.serviceCache.put(clazz.getAnnotation(ServiceDescription.class).name(), clazz.asSubclass(ServiceConfiguration.class));
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
        return new ArrayList<>(this.serviceCache.values());
    }

    public Optional<Class<? extends ServiceConfiguration>> getServiceByName(String name) {
        this.populateServiceConfigurationCache();
        return Optional.ofNullable(this.serviceCache.get(name));
    }

    public void checkDuplicateConfigurations(String username, Class<? extends ServiceConfiguration> service) {
        if (!service.getAnnotation(ServiceDescription.class).allowMultiple() &&
                this.serviceConfigurationRepo.findAllByUsernameIgnoreCase(username)
                        .stream()
                        .map(Object::getClass)
                        .anyMatch(service::equals)) {
            throw new IllegalArgumentException("Cannot duplicate service configuration " + service.getAnnotation(ServiceDescription.class).name());
        }
    }

    public WebNotificationConfiguration ensureWebNotificationConfigurationPresent(String username) {
        final List<ServiceConfiguration> configs = this.getByUsername(username);
        for (ServiceConfiguration config : configs) {
            if (config instanceof WebNotificationConfiguration wnc) {
                return wnc;
            }
        }
        WebNotificationConfiguration wc = new WebNotificationConfiguration();
        wc.setUsername(username);
        wc.setUuid(UUID.randomUUID());
        wc.setUserRegistrations(new HashSet<>());
        wc.setVerificationRequests(Set.of());
        wc.create(Map.of());
        wc.setDescription("Default Pings Web Notifications");
        this.save(wc);
        return wc;
    }

    public void delete(ServiceConfiguration config) {
        for (UserRegistration ur : config.getUserRegistrations()) {
            ur.setServiceConfiguration(null);
            ur.setRoute(null);
        }
        config.getUserRegistrations().clear();
        this.serviceConfigurationRepo.delete(config);
    }

    public List<ServiceConfiguration> getByUsername(String username) {
        return this.serviceConfigurationRepo.findAllByUsernameIgnoreCase(username);
    }
}
