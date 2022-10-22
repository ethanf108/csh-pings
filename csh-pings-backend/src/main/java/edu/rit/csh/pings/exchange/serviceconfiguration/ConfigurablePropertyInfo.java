package edu.rit.csh.pings.exchange.serviceconfiguration;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfigurablePropertyInfo {

    private String id;
    private String name;
    private String description;
    private String value;
    private ConfigurableProperty.Type type;
}
