package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.exchange.Paged;
import edu.rit.csh.pings.exchange.userregistration.UserRegistrationCreate;
import edu.rit.csh.pings.exchange.userregistration.UserRegistrationInfo;
import edu.rit.csh.pings.managers.RouteManager;
import edu.rit.csh.pings.managers.ServiceConfigurationManager;
import edu.rit.csh.pings.managers.UserRegistrationManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
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
        reg.setUuid(UUID.randomUUID());
        reg.setServiceConfiguration(config);
        reg.setUsername(user.getUsername());
        route.getUserRegistrations().add(reg);
        config.getUserRegistrations().add(reg);
        this.userRegistrationManager.save(reg);
        this.log.info("Created User Registration for User " + user.getUsername() + ", Route: " + route.getUuid() + ", App: " + route.getApplication().getUuid() + ", Service Configuration: " + config.getUuid());
    }

    @GetMapping("/api/route/{uuid}/user-registration")
    private List<UserRegistrationInfo> getUserRegistrationsForRoute(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/route/{uuid}/user-registration");
        final Route route = this.routeManager
                .findByUUID(uuid)
                .filter(n -> n.getApplication().isPublished())
                .orElseThrow();
        this.userRegistrationManager.ensureWebNotificationConfigurationAsDefault(user.getUsername(), route);
        return this.userRegistrationManager.findAllByRouteAndUsername(route, user.getUsername()).stream().map(ur -> {
            UserRegistrationInfo ret = new UserRegistrationInfo();
            ret.setRoute(ur.getRoute().getUuid());
            ret.setServiceConfiguration(ur.getServiceConfiguration().getUuid());
            ret.setUuid(ur.getUuid());
            return ret;
        }).toList();
    }

    @GetMapping("/api/user-registration")
    private Paged<UserRegistrationInfo> getUserRegistrations(
            @AuthenticationPrincipal CSHUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int length) {
        this.log.info("GET /api/user-registration");
        if (page < 0 || length < 0 || length > 100) {
            throw new IllegalArgumentException("Invalid Parameters");
        }
        final Page<UserRegistration> find = this.userRegistrationManager.findAllByUsername(user.getUsername(), page, length);
        return new Paged<>(find.stream().map(u -> {
            UserRegistrationInfo ret = new UserRegistrationInfo();
            BeanUtils.copyProperties(u, ret);
            return ret;
        }).toList(), find.getTotalElements());
    }

    @DeleteMapping("/api/user-registration/{uuid}")
    private void deleteUserRegistration(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("DELETE /api/user-registration/" + uuid);
        final UserRegistration ur = this.userRegistrationManager.findByUUID(uuid).filter(n -> n.getUsername().equalsIgnoreCase(user.getUsername())).orElseThrow();
        ur.getRoute().getUserRegistrations().remove(ur);
        ur.getServiceConfiguration().getUserRegistrations().remove(ur);
        ur.setRoute(null);
        ur.setServiceConfiguration(null);
        this.userRegistrationManager.delete(ur);
        this.log.info("Deleted User Registration " + uuid + " for user " + user.getUsername());
    }
}
