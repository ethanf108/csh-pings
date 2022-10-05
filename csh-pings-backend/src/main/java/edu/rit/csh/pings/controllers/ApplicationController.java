package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.auth.UserAccessException;
import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.Maintainer;
import edu.rit.csh.pings.exchange.Paged;
import edu.rit.csh.pings.exchange.application.ApplicationCreate;
import edu.rit.csh.pings.exchange.application.ApplicationInfo;
import edu.rit.csh.pings.external.LDAPService;
import edu.rit.csh.pings.managers.ApplicationManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ApplicationController {

    private final Log log = LogFactory.getLog("pings.application_controller");

    private final ApplicationManager applicationManager;

    private final LDAPService ldapService;

    @PostMapping("/api/application")
    public void createApplication(@AuthenticationPrincipal CSHUser user, @RequestBody ApplicationCreate data) {
        this.log.info("POST /api/application");
        if (!user.isRTP()) {
            throw new UserAccessException("Invalid permissions: must be RTP");
        }
        Application app = new Application();
        BeanUtils.copyProperties(data, app);
        app.setMaintainers(new HashSet<>());
        for (String username : data.getMaintainers()) {
            if (!this.ldapService.isValidUsername(username)) {
                throw new IllegalArgumentException("Invalid Username: " + username);
            }
            Maintainer m = new Maintainer();
            m.setUsername(username);
            m.setApplication(app);
            app.getMaintainers().add(m);
        }
        if (data.getWebURL() == null || data.getWebURL().isBlank()) {
            app.setWebURL(null);
        }
        app.setUuid(UUID.randomUUID());
        app.setExternalTokens(Set.of());
        app.setRoutes(Set.of());
        app.setPublished(false);
        this.applicationManager.save(app);
        this.log.info("Updated Application " + app.getUuid());
    }

    @GetMapping("/api/application")
    public Paged<ApplicationInfo> getApplications(
            @AuthenticationPrincipal CSHUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int length,
            @RequestParam(defaultValue = "false") boolean hidden) {
        this.log.info("GET /api/application");
        if (page < 0 || length < 0 || length > 100) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        List<ApplicationInfo> ret = new ArrayList<>(length);
        final Page<Application> query = this.applicationManager.get(page, length);
        for (Application app : query) {
            if (!app.isPublished() && !(hidden && app.isMaintainer(user))) {
                continue;
            }
            ApplicationInfo ai = new ApplicationInfo();
            BeanUtils.copyProperties(app, ai);
            ret.add(ai);
        }
        return new Paged<>(ret, query.getTotalElements());
    }

    @GetMapping("/api/application/{uuid}")
    private ApplicationInfo getApplication(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/application/" + uuid);
        Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isPublished() && !user.isRTP()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application " + uuid + " not found");
        }
        ApplicationInfo ret = new ApplicationInfo();
        BeanUtils.copyProperties(app, ret);
        return ret;
    }

    @DeleteMapping("/api/application/{uuid}")
    private void deleteApplication(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("DELETE /api/application/" + uuid);
        if (!user.getUsername().equalsIgnoreCase("ethanf108")) {
            throw new IllegalArgumentException("Deleting Applications not yet supported");
        }
        if (!user.isRTP()) {
            throw new UserAccessException("Must be RTP to delete application");
        }
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        this.applicationManager.delete(app);
        this.log.warn("Deleted Application " + app.getUuid());
    }

    @PostMapping("/api/application/{uuid}/publish")
    private void publishApplication(
            @AuthenticationPrincipal CSHUser user,
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = "true") boolean published) {
        this.log.info("POST /api/application/{uuid}/publish?published=" + published);
        final Application app = this.applicationManager
                .findByUUID(uuid)
                .filter(n -> n.isMaintainer(user))
                .orElseThrow();
        app.setPublished(published);
        this.applicationManager.save(app);
    }

    @PatchMapping("/api/application/{uuid}")
    private void changeApp(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @RequestBody ApplicationCreate data) {
        this.log.info("PATCH /api/application/" + uuid);
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be a Maintainer");
        }
        if (data.getName() != null && !data.getName().isBlank()) {
            if (app.isPublished()) {
                throw new IllegalArgumentException("App cannot be changed while Published");
            }
            app.setName(data.getName());
        }
        if (data.getDescription() != null) {
            app.setDescription(data.getDescription());
        }
        app.setWebURL(data.getWebURL());
        this.applicationManager.save(app);
    }
}
