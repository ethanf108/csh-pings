package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.ExternalToken;
import edu.rit.csh.pings.repos.ExternalTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalTokenManager {

    private final ExternalTokenRepo externalTokenRepo;

    public Optional<ExternalToken> findByToken(String token) {
        return Optional.ofNullable(this.externalTokenRepo.findByToken(token));
    }
}
