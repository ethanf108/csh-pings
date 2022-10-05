package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ServiceConfiguration;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.repos.VerificationRequestRepo;
import edu.rit.csh.pings.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
        vr.setExpire(LocalDateTime.now().plus(5, ChronoUnit.DAYS));
        sc.getVerificationRequests().add(vr);
        this.serviceConfigurationManager.save(sc);
        return vr;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    private void deleteExpired() {
        final List<VerificationRequest> vrs = this.verificationRequestRepo.findAllByExpireBefore(LocalDateTime.now());
        this.verificationRequestRepo.deleteAll(vrs);
    }
}
