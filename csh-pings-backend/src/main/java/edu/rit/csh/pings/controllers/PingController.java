package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.entities.ExternalToken;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.UserRegistration;
import edu.rit.csh.pings.exchange.ping.PingData;
import edu.rit.csh.pings.external.ExternalDispatchService;
import edu.rit.csh.pings.external.LDAPService;
import edu.rit.csh.pings.managers.ExternalTokenManager;
import edu.rit.csh.pings.managers.RouteManager;
import edu.rit.csh.pings.managers.UserRegistrationManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class PingController {

    private final Log log = LogFactory.getLog("pings.ping_controller");

    private final ExternalTokenManager externalTokenManager;
    private final UserRegistrationManager userRegistrationManager;
    private final RouteManager routeManager;
    private final LDAPService ldapService;
    private final ExternalDispatchService externalDispatchService;

    @GetMapping("/service/status")
    private boolean checkServiceStatus(@RequestHeader(name = "Authorization") String tok) {
        return this.externalTokenManager.findByToken(tok).isPresent();
    }

    @PostMapping("/service/route/{routeUUID}/ping")
    private void ping(@RequestHeader(name = "Authorization") String tok, @PathVariable UUID routeUUID, @RequestBody PingData pingData) {
        final ExternalToken token = this.externalTokenManager.findByToken(tok).orElseThrow();
        if (!this.ldapService.isValidUsername(pingData.getUsername())) {
            throw new NoSuchElementException("User '" + pingData.getUsername() + "' does not exist");
        }
        final Route route = this.routeManager.findByUUID(routeUUID).orElseThrow();
        this.userRegistrationManager.ensureWebNotificationConfigurationAsDefault(pingData.getUsername(), route);
        final List<UserRegistration> registrations = this.userRegistrationManager.findAllByRouteAndUsername(route, pingData.getUsername());
        for (UserRegistration registration : registrations) {
            this.externalDispatchService.getExternalService(registration.getServiceConfiguration()).sendPing(registration.getRoute(), registration.getServiceConfiguration(), pingData.getBody());
        }
    }
}
