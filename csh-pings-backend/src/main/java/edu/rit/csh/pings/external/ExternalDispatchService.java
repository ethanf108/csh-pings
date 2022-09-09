package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.EmailServiceConfiguration;
import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.WebNotificationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is stupid, but I don't know a better way to do this.
 * TODO: Make this class not necessary
 */
@Service
@RequiredArgsConstructor
public class ExternalDispatchService {

    private final EmailService emailService;
    private final WebNotificationService webNotificationService;

    @SuppressWarnings("unchecked") //lmao
    public <T extends ServiceConfiguration> ExternalService<T> getExternalService(T config) {
        if (config instanceof EmailServiceConfiguration emailConfig) {
            return (ExternalService<T>) this.emailService;
        } else if (config instanceof WebNotificationConfiguration) {
            return (ExternalService<T>) this.webNotificationService;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid Service Configuration");
        }
    }
}
