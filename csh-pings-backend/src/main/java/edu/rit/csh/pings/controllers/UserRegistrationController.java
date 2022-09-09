package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.exchange.userregistration.UserRegistrationCreate;
import edu.rit.csh.pings.exchange.userregistration.UserRegistrationInfo;
import edu.rit.csh.pings.managers.RouteManager;
import edu.rit.csh.pings.managers.ServiceConfigurationManager;
import edu.rit.csh.pings.managers.UserRegistrationManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserRegistrationController {

    private final Log log = LogFactory.getLog("pings.user_registration_controller");
    private final UserRegistrationManager userRegistrationManager;
    private final RouteManager routeManager;
    private final ServiceConfigurationManager serviceConfigurationManager;

    @PostMapping("/api/user-registration")
    private void createUserRegistration(@AuthenticationPrincipal CSHUser user, @RequestBody UserRegistrationCreate create) {
        this.log.info("POST /api/user-registration");
        final Route route = this.routeManager.findByUUID(create.getRoute()).filter(n -> n.getApplication().isPublished()).orElseThrow();
        final ServiceConfiguration config = this.serviceConfigurationManager
                .findByUUID(create.getServiceConfiguration())
                .filter(n -> n.getUsername().equalsIgnoreCase(user.getUsername()))
                .orElseThrow();
        if (!config.isVerified()) {
            throw new IllegalArgumentException("Service configuration is not verified");
        }
        UserRegistration reg = new UserRegistration();
        reg.setRoute(route);
        reg.setServiceConfiguration(config);
        reg.setUsername(user.getUsername());
        route.getUserRegistrations().add(reg);
        config.getUserRegistrations().add(reg);
        this.userRegistrationManager.save(reg);
    }

    @GetMapping("/api/route/{uuid}/user-registration")
    private List<UserRegistrationInfo> getUserRegistrationsForRoute(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/route/{uuid}/user-registration");
        return this.userRegistrationManager.findAllByRouteAndUsername(this.routeManager
                .findByUUID(uuid)
                .filter(n -> n.getApplication().isPublished())
                .orElseThrow(), user.getUsername()).stream().map(ur -> {
            UserRegistrationInfo ret = new UserRegistrationInfo();
            ret.setRoute(ur.getRoute().getUuid());
            ret.setServiceConfiguration(ur.getServiceConfiguration().getUuid());
            return ret;
        }).toList();
    }

}
