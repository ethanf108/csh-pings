package edu.rit.csh.pings.exchange.webnotification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class WebNotificationInfo {

    private UUID uuid;
    private String body;
    private UUID applicationUUID;
    private LocalDateTime date;
    private boolean unread;
}
