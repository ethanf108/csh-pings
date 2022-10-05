package edu.rit.csh.pings.exchange.serviceconfiguration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ServiceConfigurationInfo {

    private UUID uuid;
    private String description;
    private ServiceInfo service;
    private boolean verified;
    private List<ConfigurablePropertyInfo> properties;
}
