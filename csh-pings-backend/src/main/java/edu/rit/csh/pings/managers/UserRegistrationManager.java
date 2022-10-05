package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.entities.WebNotificationConfiguration;
import edu.rit.csh.pings.repos.UserRegistrationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationManager {

    /**
     * TODO find a way to not have to use a LOCK lol
     */
    private static final Object LOCK = new Object();

    private final UserRegistrationRepo userRegistrationRepo;
    private final ServiceConfigurationManager serviceConfigurationManager;

    public Optional<UserRegistration> findByUUID(UUID uuid) {
        return Optional.ofNullable(this.userRegistrationRepo.findByUuid(uuid));
    }

    public List<UserRegistration> findAllByRouteAndUsername(Route route, String username) {
        return this.userRegistrationRepo.findAllByRouteAndUsername(route, username);
    }

    public Page<UserRegistration> findAllByUsername(String username, int page, int size) {
        return this.userRegistrationRepo.findAllByUsername(username, PageRequest.of(page, size));
    }

    public void save(UserRegistration ur) {
        this.userRegistrationRepo.save(ur);
    }

    public void delete(UserRegistration ur) {
        this.userRegistrationRepo.delete(ur);
    }

    /**
     * All pings go to Web Notifications as a default, this method ensures this.
     *
     * @param username the username to bind to
     * @param route    the {@link edu.rit.csh.pings.entities.Route} to bind to
     */
    public void ensureWebNotificationConfigurationAsDefault(String username, Route route) {
        synchronized (LOCK) {
            final List<UserRegistration> registrations = this.findAllByRouteAndUsername(route, username);
            if (registrations.isEmpty()) {
                UserRegistration registration = new UserRegistration();
                final WebNotificationConfiguration wc = this.serviceConfigurationManager.ensureWebNotificationConfigurationPresent(username);
                registration.setServiceConfiguration(wc);
                registration.setRoute(route);
                registration.setUsername(username);
                registration.setUuid(UUID.randomUUID());
                wc.getUserRegistrations().add(registration);
                this.serviceConfigurationManager.save(wc);
            }
        }
    }
}
