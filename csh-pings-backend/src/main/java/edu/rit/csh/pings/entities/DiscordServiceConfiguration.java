package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import edu.rit.csh.pings.servicereflect.ServiceDescription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Map;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ServiceDescription(id = "discord", name = "Discord", description = "Send pings to your Discord Account.")
public final class DiscordServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(id = "discord_username", name = "Discord Username", type = ConfigurableProperty.Type.TEXT)
    private String discordUsername;

    @Column
    @ConfigurableProperty(id = "discord_discriminator", name = "Discord Discriminator", description = "The 4 numbers to uniquely identify users with the same username" ,type = ConfigurableProperty.Type.TEXT)
    private String discordDiscriminator;

    @Override
    public void create(Map<String, String> properties) {
        this.discordUsername = Optional.ofNullable(properties.get("discord_username")).orElseThrow(() -> new IllegalArgumentException("Missing Property discord_username"));
        this.discordDiscriminator = Optional.ofNullable(properties.get("discord_discriminator")).orElseThrow(() -> new IllegalArgumentException("Missing Property discord_discriminator"));
    }
}
