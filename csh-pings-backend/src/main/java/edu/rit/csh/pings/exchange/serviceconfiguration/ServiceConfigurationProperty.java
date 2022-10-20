package edu.rit.csh.pings.exchange.serviceconfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceConfigurationProperty {

    private String id;
    private String name;
    private String description;
    private String type;
    private List<String> enumValues;

}
