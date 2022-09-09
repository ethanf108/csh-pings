package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pings_external_token")
@Getter
@Setter
@NoArgsConstructor
public class ExternalToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application", nullable = false)
    private Application application;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String note;
}
