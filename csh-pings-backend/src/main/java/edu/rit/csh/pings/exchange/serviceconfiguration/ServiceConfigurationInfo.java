package edu.rit.csh.pings.exchange.serviceconfiguration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class ServiceConfigurationInfo {

    private String name;
    private boolean verified;
    private Map<String, String> properties;
}
