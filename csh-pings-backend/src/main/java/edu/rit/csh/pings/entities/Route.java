package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pings_route")
@Getter
@Setter
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application")
    private Application application;

    @OneToMany(mappedBy = "route", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRegistration> userRegistrations;

    @OneToMany(mappedBy = "route", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<WebNotification> webNotifications;
}
