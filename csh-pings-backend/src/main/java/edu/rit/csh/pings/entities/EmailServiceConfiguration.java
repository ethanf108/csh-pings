package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "pings_email_service_configuration")
@NoArgsConstructor
@Getter
@Setter
@ConfigurableProperty(name = "email", description = "Email Service")
public final class EmailServiceConfiguration extends ServiceConfiguration implements ServiceConfigurationMarker {

    @Column(nullable = false)
    @ConfigurableProperty(name = "email", description = "Email Address")
    private String toEmail;

    @Override
    public void create(Map<String, String> properties) {
        if (!properties.containsKey("email")) {
            throw new IllegalArgumentException("Missing email property");
        }
        this.setToEmail(properties.get("email"));
    }
}
