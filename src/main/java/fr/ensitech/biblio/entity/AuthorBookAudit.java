package fr.ensitech.biblio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "author_book_audit")
@Getter
@Setter
public class AuthorBookAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId;
    private Long bookId;
    private String operationType;
    private String associationDate;
    private String createdAt;
}
