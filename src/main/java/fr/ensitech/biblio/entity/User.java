package fr.ensitech.biblio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "users", catalog = "biblio_database")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstname", nullable = false, length = 48)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 48)
    private String lastname;

    @Column(name = "email", nullable = false, length = 48, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 48)
    private String password;

    @Column(name = "role", nullable = false, length = 1)
    private String role;

    @Column(name = "birthdate", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Column(name = "active", nullable = false)
    private boolean active;

}
