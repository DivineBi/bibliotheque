package fr.ensitech.biblio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users",
        indexes = {
            @Index(name = "idx_user_email", columnList = "email"),
            @Index(name = "idx_user_active", columnList = "active"),
            @Index(name = "idx_user_role", columnList = "role"),
        }
)
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 48)
    private String firstname;

    @Column(nullable = false, length = 48)
    private String lastname;

    @Column(nullable = false, length = 48, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column
    private LocalDate birthdate;

    @Column(nullable = false)
    private boolean active;

    //@Column(name = "password_last_updated")
    //private LocalDateTime passwordLastUpdated;

    // Relation vers SecurityQuestion
    //@ManyToOne
    //@JoinColumn(name = "security_question_id", nullable = false)
    //private SecurityQuestion securityQuestion;

    //@Column(name = "security_answer", nullable = false, length = 255)
   // private String securityAnswer; // réponse stockée hachée

    // Relation vers l'historique des mots de passe
   // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<PasswordHistory> passwordHistory = new ArrayList<>();
}
