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
@ServiceDescription(id = "web", name = "Web Notifications", description = "Send notifications to pings.csh (default)", allowMultiple = false)
public final class WebNotificationConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Override
    public void create(Map<String, String> properties) {
        this.setVerified(true);
    }
}
