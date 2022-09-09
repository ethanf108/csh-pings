package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MainController {

    @GetMapping("/status")
    private String status() {
        return "You are connected :)";
    }

    @GetMapping("/api/status")
    private String apiStatus(@AuthenticationPrincipal CSHUser user) {
        return "Hi " + user.getUsername() + ", you are connected and authenticated :)";
    }
}
