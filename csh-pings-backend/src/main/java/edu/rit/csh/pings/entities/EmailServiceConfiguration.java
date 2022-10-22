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
@ServiceDescription(id = "email", name = "Email", description = "Send ping to an email address")
public final class EmailServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(id = "email", name = "Email Address", description = "Email Address", type = ConfigurableProperty.Type.EMAIL)
    private String toEmail;

    @Override
    public void create(Map<String, String> properties) {
        if (!properties.containsKey("email")) {
            throw new IllegalArgumentException("Missing email property");
        }
        this.setToEmail(properties.get("email"));
    }
}
