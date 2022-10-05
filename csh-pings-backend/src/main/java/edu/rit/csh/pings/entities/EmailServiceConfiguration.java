package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ServiceDescription(name = "email", description = "Email Service")
public final class EmailServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(name = "email", description = "Email Address", type = ConfigurableProperty.Type.EMAIL)
    private String toEmail;

    @Override
    public void create(Map<String, String> properties) {
        if (!properties.containsKey("email")) {
            throw new IllegalArgumentException("Missing email property");
        }
        this.setToEmail(properties.get("email"));
    }
}
