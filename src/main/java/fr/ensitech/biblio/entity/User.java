package fr.ensitech.biblio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
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

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role", nullable = false, length = 1)
    private String role;

    @Column(name = "birthdate", nullable = true)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern =  "yyyy-MM-dd")
    private Date birthdate;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    // Relation vers SecurityQuestion
    @ManyToOne
    @JoinColumn(name = "security_question_id", nullable = false)
    private SecurityQuestion securityQuestion;

    @Column(name = "security_answer", nullable = false, length = 255)
    private String securityAnswer; // réponse stockée hachée

    // Relation vers l'historique des mots de passe
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordHistory> passwordHistory = new ArrayList<>();
}
