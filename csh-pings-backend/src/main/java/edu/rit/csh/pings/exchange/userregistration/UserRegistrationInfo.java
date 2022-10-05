package edu.rit.csh.pings.exchange.userregistration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationInfo {

    private UUID uuid;
    private UUID route;
    private UUID serviceConfiguration;
}
