package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.entities.WebNotificationConfiguration;
import edu.rit.csh.pings.repos.UserRegistrationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationManager {

    private final UserRegistrationRepo userRegistrationRepo;

    public List<UserRegistration> findAllByRouteAndUsername(Route route, String username) {
        return this.userRegistrationRepo.findAllByRouteAndUsername(route, username);
    }

    public void save(UserRegistration ur) {
        this.userRegistrationRepo.save(ur);
    }

    /**
     * All pings go to Web Notifications as a default, this method ensures this.
     *
     * @param username the username to bind to
     * @param route    the {@link edu.rit.csh.pings.entities.Route} to bind to
     */
    public void ensureWebNotificationConfigurationAsDefault(String username, Route route) {
        final List<UserRegistration> registrations = this.findAllByRouteAndUsername(route, username);
        if (registrations.isEmpty()) {
            UserRegistration registration = new UserRegistration();
            WebNotificationConfiguration wc = new WebNotificationConfiguration();
            wc.setUsername(username);
            wc.setUuid(UUID.randomUUID());
            wc.setUserRegistrations(Set.of(registration));
            wc.setVerificationRequests(Set.of());
            wc.create(Map.of());
            registration.setServiceConfiguration(wc);
            registration.setRoute(route);
            this.save(registration);
        }
    }
}
