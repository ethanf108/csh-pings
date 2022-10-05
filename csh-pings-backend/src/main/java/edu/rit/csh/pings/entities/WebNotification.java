package edu.rit.csh.pings.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pings_web_notification")
@NoArgsConstructor
@Setter
@Getter
public class WebNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route")
    private Route route;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean unread;

}
