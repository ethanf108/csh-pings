package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.auth.UserAccessException;
import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.ExternalToken;
import edu.rit.csh.pings.exchange.token.ExternalTokenCreate;
import edu.rit.csh.pings.exchange.token.ExternalTokenInfo;
import edu.rit.csh.pings.managers.ApplicationManager;
import edu.rit.csh.pings.managers.ExternalTokenManager;
import edu.rit.csh.pings.util.Util;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ExternalTokenController {

    private final ApplicationManager applicationManager;
    private final ExternalTokenManager externalTokenManager;

    private final Log log = LogFactory.getLog("pings.external_token_controller");

    @GetMapping("/api/application/{uuid}/token")
    public List<ExternalTokenInfo> getTokens(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/application/" + uuid + "/token");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be maintainer");
        }
        return app.getExternalTokens().stream().map(n -> {
            ExternalTokenInfo ret = new ExternalTokenInfo();
            BeanUtils.copyProperties(n, ret);
            return ret;
        }).toList();
    }

    @PostMapping("/api/application/{uuid}/token")
    public void addToken(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @RequestBody ExternalTokenCreate create) {
        this.log.info("POST /api/application/" + uuid + "/token");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be maintainer");
        }
        final ExternalToken token = new ExternalToken();
        BeanUtils.copyProperties(create, token);
        app.getExternalTokens().add(token);
        token.setToken(Util.generateNoise());
        token.setApplication(app);
        this.applicationManager.save(app);
        this.log.info("Added token to app " + app.getUuid());
    }

    @DeleteMapping("/api/token/{token}")
    private void deleteToken(@AuthenticationPrincipal CSHUser user, @PathVariable String token) {
        this.log.info("DELETE /api/token/" + token);
        final ExternalToken tok = this.externalTokenManager.findByToken(token).orElseThrow();
        final Application app = tok.getApplication();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be maintainer");
        }
        app.getExternalTokens().remove(tok);
        this.applicationManager.save(app);
        this.log.info("Deleted token for app" + app.getUuid());
    }
}
