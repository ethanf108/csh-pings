package edu.rit.csh.pings.entities;

import edu.rit.csh.pings.servicereflect.ConfigurableProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean verified;

    @OneToMany(mappedBy = "serviceConfiguration", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<VerificationRequest> verificationRequests;

    @OneToMany(mappedBy = "serviceConfiguration", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRegistration> userRegistrations;

    public abstract void create(Map<String, String> properties);

    public ConfigurableProperty getInfo() {
        return this.getClass().getAnnotation(ConfigurableProperty.class);
    }
}
