package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.repos.ApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationManager {

    private final ApplicationRepo applicationRepo;

    public void save(Application a) {
        this.applicationRepo.save(a);
    }

    public Page<Application> get(int page, int size) {
        return this.applicationRepo.findAllByOrderByName(PageRequest.of(page, size));
    }

    public Optional<Application> findByUUID(UUID uuid) {
        return Optional.ofNullable(this.applicationRepo.findByUuid(uuid));
    }

    public void delete(Application app) {
        this.applicationRepo.delete(app);
    }
}
