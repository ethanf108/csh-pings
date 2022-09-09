package edu.rit.csh.pings.exchange.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class RouteInfo {

    private UUID uuid;
    private String name;
    private String description;
}
