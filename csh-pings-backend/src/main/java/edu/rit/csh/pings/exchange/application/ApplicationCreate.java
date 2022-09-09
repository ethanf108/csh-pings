package edu.rit.csh.pings.exchange.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ApplicationCreate implements Serializable {

    private String name;
    private String description;
    private List<String> maintainers;
}
