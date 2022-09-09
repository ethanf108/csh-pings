package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "pings_web_notification_configuration")
@NoArgsConstructor
@Setter
@Getter
@ConfigurableProperty(name = "web", description = "View pings on pings.csh")
public final class WebNotificationConfiguration extends ServiceConfiguration implements ServiceConfigurationMarker {

    @Override
    public void create(Map<String, String> properties) {
        this.setVerified(true);
    }
}
