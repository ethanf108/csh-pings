package edu.rit.csh.pings.exchange.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationInfo {

    private UUID uuid;
    private String name;
    private String description;
    private String webURL;
    private boolean published;
}
