package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.BasicSMSServiceConfiguration;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicSMSService implements ExternalService<BasicSMSServiceConfiguration> {

    private final EmailService emailService;
    private final VerificationRequestManager verificationRequestManager;

    private String getEmail(String carrier) {
        return switch (carrier.toLowerCase()) {
            case "at&t", "att" -> "mms.att.net";
            case "boost mobile" -> "myboostmobile.com";
            case "verizon" -> "vzwpix.com";
            case "tmobile", "t-mobile" -> "tmomail.net";
            case "sprint" -> "pm.sprint.com";
            case "google fi" -> "msg.fi.google.com";
            default -> throw new IllegalArgumentException("Invalid Carrier");
        };
    }

    @Override
    public void sendPing(Route route, BasicSMSServiceConfiguration config, String body) {
        final String email = this.getEmail(config.getCarrier());
        this.emailService.sendSMSEmail(config.getPhoneNum() + "@" + email, route.getApplication().getName(), route.getName(), body);
    }

    @Override
    public void sendVerification(BasicSMSServiceConfiguration config) {
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        final String email = this.getEmail(config.getCarrier());
        this.emailService.sendSMSEmail(config.getPhoneNum() + "@" + email, "Pings", "Verify Phone Number",
                "To verify this phone number, go to https://pings.csh.rit.edu/verify?token=" + vr.getToken());
    }
}
