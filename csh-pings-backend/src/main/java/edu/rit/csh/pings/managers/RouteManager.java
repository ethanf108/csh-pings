package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.repos.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteManager {

    private final RouteRepository routeRepository;

    public Optional<Route> findByUUID(UUID uuid) {
        return Optional.ofNullable(this.routeRepository.findByUuid(uuid));
    }
}
