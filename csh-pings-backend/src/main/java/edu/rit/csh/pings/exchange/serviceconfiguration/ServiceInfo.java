package edu.rit.csh.pings.exchange.serviceconfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceInfo {

    private String id;
    private String name;
    private String description;
}
