package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Map;

@Entity
@NoArgsConstructor
@Setter
@Getter
@ServiceDescription(name = "web", description = "Web Notifications")
public final class WebNotificationConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Override
    public void create(Map<String, String> properties) {
        this.setVerified(true);
    }
}
