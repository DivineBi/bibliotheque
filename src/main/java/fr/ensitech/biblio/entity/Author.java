package fr.ensitech.biblio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "author")
@Setter @Getter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 48, nullable = false)
    private String firstname;

    @Column(length = 48, nullable = false)
    private String lastname;

    @Column(name = "birthdate", nullable = true)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern =  "yyyy-MM-dd")
    private Date birthdate;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Book> books = new HashSet<Book>();
}
