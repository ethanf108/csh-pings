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
@ServiceDescription(id = "csh-slack", name = "CSH Slack", description = "Send Pings to your CSH Slack account", allowMultiple = false)
public final class CSHSlackServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    private String slackUID;

    @ConfigurableProperty(
            id = "slack-email",
            name = "Slack Email",
            description = "The Email tied to your CSH Slack account",
            type = ConfigurableProperty.Type.EMAIL,
            validationRegex = ".+@.+")
    @Column
    private String slackEmail;

    @Override
    public void create(Map<String, String> properties) {
        final String slackEmail = properties.get("slack-email");
        if (!slackEmail.matches(".+@.+")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.setSlackEmail(slackEmail);
    }
}
