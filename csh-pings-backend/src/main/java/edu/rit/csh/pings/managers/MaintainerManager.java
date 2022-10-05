package edu.rit.csh.pings.managers;

import edu.rit.csh.pings.repos.MaintainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaintainerManager {

    private final MaintainerRepo maintainerRepo;

}
