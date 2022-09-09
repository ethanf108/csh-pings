package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.ServiceConfiguration;

public interface ExternalService<T extends ServiceConfiguration> {

    void sendPing(Route route, T config, String body);

    void sendVerification(T config);
}
