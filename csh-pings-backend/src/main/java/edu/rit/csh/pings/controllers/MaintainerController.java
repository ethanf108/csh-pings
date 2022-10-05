package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.CSHUser;
import edu.rit.csh.pings.auth.UserAccessException;
import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.Maintainer;
import edu.rit.csh.pings.exchange.maintainer.MaintainerCreate;
import edu.rit.csh.pings.exchange.maintainer.MaintainerInfo;
import edu.rit.csh.pings.managers.ApplicationManager;
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
public class MaintainerController {

    private final Log log = LogFactory.getLog("pings.maintainer_controller");
    private final ApplicationManager applicationManager;

    @GetMapping("/api/application/{uuid}/maintainer")
    private List<MaintainerInfo> getMaintainers(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid) {
        this.log.info("GET /api/application/" + uuid + "/maintainer");
        return this.applicationManager.findByUUID(uuid).orElseThrow().getMaintainers().stream().map(n -> {
            MaintainerInfo ret = new MaintainerInfo();
            BeanUtils.copyProperties(n, ret);
            return ret;
        }).toList();
    }

    @PostMapping("/api/application/{uuid}/maintainer")
    private void addMaintainer(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @RequestBody MaintainerCreate create) {
        this.log.info("POST /api/application/" + uuid + "/maintainer");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user)) {
            throw new UserAccessException("Must be maintainer");
        }
        Maintainer m = new Maintainer();
        BeanUtils.copyProperties(create, m);
        m.setApplication(app);
        app.getMaintainers().add(m);
        this.applicationManager.save(app);
        this.log.info("Added mintaineer " + m.getUsername() + " to App " + app.getUuid());
    }

    @DeleteMapping("/api/application/{uuid}/maintainer/{username}")
    private void deleteMaintainer(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @PathVariable String username) {
        this.log.info("DELETE /api/application/" + uuid + "/maintainer" + username);
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!app.isMaintainer(user) && !user.getUsername().equalsIgnoreCase(username)) {
            throw new UserAccessException("Cannot remove maintainer: invalid permissions");
        }
        final Maintainer m = app.getMaintainers().stream().filter(n -> n.getUsername().equalsIgnoreCase(username)).findAny().orElseThrow();
        app.getMaintainers().remove(m);
        this.applicationManager.save(app);
        this.log.info("Removed Maintainer " + username + " from App " + app.getUuid());
    }

    @PatchMapping("/api/application/{uuid}/maintainer")
    private void changeMaintainers(@AuthenticationPrincipal CSHUser user, @PathVariable UUID uuid, @RequestBody List<MaintainerCreate> data) {
        this.log.info("PATCH /api/application/" + uuid + "/maintainer");
        final Application app = this.applicationManager.findByUUID(uuid).orElseThrow();
        if (!user.isRTP()) {
            throw new UserAccessException("Must be a Pings Admin");
        }
        app.getMaintainers().clear();
        for (MaintainerCreate mc : data) {
            Maintainer m = new Maintainer();
            m.setApplication(app);
            BeanUtils.copyProperties(mc, m);
            app.getMaintainers().add(m);
        }
        this.applicationManager.save(app);
        this.log.info("Overwrote maintainers for App " + app.getUuid());
        this.log.debug("New Maintainers for App " + app.getUuid() + ": " + data.stream().map(MaintainerCreate::getUsername).toList());
    }
}
