package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.exchange.csh.CSHUserInfo;
import edu.rit.csh.pings.external.LDAPService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CSHController {

    private final LDAPService ldapService;

    @GetMapping("/api/csh/user")
    private CSHUserInfo getUserInfo(@AuthenticationPrincipal CSHUser user) {
        CSHUserInfo ret = new CSHUserInfo();
        ret.setUsername(user.getUsername());
        ret.setRtp(user.isRTP());
        ret.setFullName(user.getFullName());
        return ret;
    }

    @GetMapping("/api/csh/user/{username}/exists")
    private boolean userExists(@PathVariable String username) {
        return this.ldapService.isValidUsername(username);
    }

    /**
     * Get users who match query
     *
     * @param query the username to match (beginning)
     * @return list of usernames
     */
    @GetMapping("/api/csh/search")
    private List<CSHUserInfo> username(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (offset < 0 || limit < 0 || limit > 100) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        final List<CSHUserInfo> ret = this.ldapService.searchUsers(query);
        return ret.subList(Math.min(ret.size(), offset), Math.min(ret.size(), offset + limit));
    }

    @GetMapping("/api/csh/user/{username}")
    private CSHUserInfo getUser(@PathVariable String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("Must be valid username");
        }
        final CSHUserInfo ret = this.ldapService.getUser(username);
        if (ret == null) {
            throw new NoSuchElementException();
        }
        return ret;
    }
}
