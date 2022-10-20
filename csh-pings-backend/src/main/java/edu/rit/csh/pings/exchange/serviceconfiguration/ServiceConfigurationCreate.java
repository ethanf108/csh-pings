package edu.rit.csh.pings.exchange.serviceconfiguration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class ServiceConfigurationCreate {

    private String serviceId;
    private String description;
    private Map<String, String> properties;
}
