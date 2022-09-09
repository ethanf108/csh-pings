package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.auth.CSHUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pings_application")
@Getter
@Setter
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean published;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Maintainer> maintainers;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ExternalToken> externalTokens;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Route> routes;

    public boolean isMaintainer(CSHUser u) {
        return u.isRTP() || this.maintainers.stream().anyMatch(n -> n.getUsername().equalsIgnoreCase(u.getUsername()));
    }
}
