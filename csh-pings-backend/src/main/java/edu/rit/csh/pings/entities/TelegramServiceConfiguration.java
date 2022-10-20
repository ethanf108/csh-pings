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
@ServiceDescription(id = "telegram", name = "Telegram Messenger", description = "Send pings to your Telegram Account.")
public final class TelegramServiceConfiguration extends ServiceConfiguration implements ServiceMarker {

    @Column
    @ConfigurableProperty(
            id = "telegram_username",
            name = "Telegram Username",
            description = "Valid Telegram Username. Please <a href=\"https://t.me/cshpings\" target=\"_blank\">add the Pings bot</a> on Telegram before creating",
            type = ConfigurableProperty.Type.TEXT)
    private String telegramUsername;

    @Column
    private Long telegramId;

    @Override
    public void create(Map<String, String> properties) {
        this.telegramUsername = Optional.ofNullable(properties.get("telegram_username")).orElseThrow(() -> new IllegalArgumentException("Missing Property"));
        this.telegramId = null;
    }
}
