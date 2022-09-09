package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pings_verification_request")
@NoArgsConstructor
@Getter
@Setter
public class VerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_configuration")
    private ServiceConfiguration serviceConfiguration;

    @Column(nullable = false, unique = true, columnDefinition = "char(255)")
    private String token;
}
