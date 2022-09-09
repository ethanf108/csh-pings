package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.repos.VerificationRequestRepo;
import edu.rit.csh.pings.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationRequestManager {

    private final ServiceConfigurationManager serviceConfigurationManager;
    private final VerificationRequestRepo verificationRequestRepo;

    public Optional<VerificationRequest> findByToken(String token) {
        return Optional.ofNullable(this.verificationRequestRepo.findByToken(token));
    }

    public VerificationRequest generateVerification(ServiceConfiguration sc) {
        VerificationRequest vr = new VerificationRequest();
        vr.setToken(Util.generateNoise());
        vr.setServiceConfiguration(sc);
        sc.getVerificationRequests().add(vr);
        this.serviceConfigurationManager.save(sc);
        return vr;
    }
}
