package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.Getter;

import javax.persistence.Column;
import java.util.Map;
import java.util.Optional;

@Getter
public non-sealed class DiscordServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(name = "discord_username", description = "Discord Username", type = ConfigurableProperty.Type.TEXT)
    String discordUsername;

    @Column
    @ConfigurableProperty(name = "discord_discriminator", description = "Discord Discriminator", type = ConfigurableProperty.Type.TEXT)
    String discordDiscriminator;

    @Override
    public void create(Map<String, String> properties) {
        this.discordUsername = Optional.ofNullable(properties.get("discord_username")).orElseThrow(() -> new IllegalArgumentException("Missing Property"));
        this.discordDiscriminator = Optional.ofNullable(properties.get("discord_discriminator")).orElseThrow(() -> new IllegalArgumentException("Missing Property"));
    }
}
