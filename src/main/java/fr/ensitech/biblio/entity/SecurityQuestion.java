package fr.ensitech.biblio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "security_questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SecurityQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 128, unique = true)
    private String question;

    @OneToMany(mappedBy = "securityQuestion", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private List<User> users = new ArrayList<>();
}
