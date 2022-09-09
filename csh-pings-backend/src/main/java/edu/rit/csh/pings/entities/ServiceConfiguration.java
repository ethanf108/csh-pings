package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@Table(name = "pings_service_configuration")
@NoArgsConstructor
@Getter
@Setter
public abstract class ServiceConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(nullable = false, unique = true)
    private long id;

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    private UUID uuid;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean verified;

    @OneToMany(mappedBy = "serviceConfiguration", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<VerificationRequest> verificationRequests;

    @OneToMany(mappedBy = "serviceConfiguration", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRegistration> userRegistrations;

    public abstract void create(Map<String, String> properties);
}
