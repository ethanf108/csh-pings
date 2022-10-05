package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MainController {

    private final Log log = LogFactory.getLog("pings.main_controller");

    @GetMapping("/status")
    private String status() {
        this.log.info("GET /status");
        return "You are connected :)";
    }

    @GetMapping("/api/status")
    private String apiStatus(@AuthenticationPrincipal CSHUser user) {
        this.log.info("GET /api/status");
        return "Hi " + user.getUsername() + ", you are connected and authenticated :)";
    }
}
