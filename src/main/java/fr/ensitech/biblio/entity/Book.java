package fr.ensitech.biblio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "books",
        indexes = {
                @Index(name = "idx_book_title", columnList = "title"),
                @Index(name = "idx_book_category", columnList = "category"),
                @Index(name = "idx_book_language", columnList = "language"),
                @Index(name = "idx_book_published", columnList = "published"),
        }
)
@Setter @Getter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false,  length = 100)
    private String editor;

    @Column(nullable = false)
    private LocalDate publicationDate;

    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int nbPages;

    @Column(nullable = false, length = 48)
    private String category;

    @Column(nullable = false, length = 3)
    private String language;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "author_book",
                joinColumns = @JoinColumn(name = "book_id"), //FK vers Book
                inverseJoinColumns = @JoinColumn(name = "author_id")) // FK vers Author

    @Builder.Default
    private Set<Author> authors = new HashSet<Author>();

    @Column
    private int quantity;
}
