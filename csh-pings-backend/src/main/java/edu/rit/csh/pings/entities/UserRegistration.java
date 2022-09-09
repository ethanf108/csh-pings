package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class basically represents a Many-to-Many relationship between {@link edu.rit.csh.pings.entities.Route} and {@link edu.rit.csh.pings.entities.ServiceConfiguration}, with an extra username column.
 * However, the username column already exists in the {@link edu.rit.csh.pings.entities.ServiceConfiguration}, making it convenient but redundant.
 * TODO: consider removing this class and replacing it with Bi-directional Many-to-Many's
 */
@Entity
@Table(name = "pings_registration")
@NoArgsConstructor
@Getter
@Setter
public class UserRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route")
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_configuration")
    private ServiceConfiguration serviceConfiguration;
}
