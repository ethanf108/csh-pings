package edu.rit.csh.pings.controllers;


import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.ServiceConfigurationManager;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class VerificationRequestController {

    private final Log log = LogFactory.getLog("pings.verification_request_controller");

    private final ServiceConfigurationManager serviceConfigurationManager;
    private final VerificationRequestManager verificationRequestManager;

    /**
     * Verifies a Service Configuration.
     * TODO: For some reason, this may change the UUID of the service configuration.
     *
     * @param token the token to verify
     */
    @PostMapping("/api/verify/")
    private void verify(@AuthenticationPrincipal CSHUser user, @RequestParam String token) {
        this.log.info("POST /api/verify");
        final VerificationRequest vr = this.verificationRequestManager.findByToken(token).orElseThrow();
        ServiceConfiguration config = vr.getServiceConfiguration();
        this.log.debug("Verifying " + config.getUuid());
        this.log.debug("Clearing " + config.getVerificationRequests().size() + " verification requests");
        config.getVerificationRequests().clear();
        config.setVerified(true);
        this.serviceConfigurationManager.save(config);
        this.log.info("Verified Service Configuration " + config.getUuid() + " for User " + user.getUsername());
    }
}
