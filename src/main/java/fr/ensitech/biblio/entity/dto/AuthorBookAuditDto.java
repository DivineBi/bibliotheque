package fr.ensitech.biblio.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorBookAuditDto {

    private Long id;
    private Long authorId;
    private Long bookId;
    private String operationType;
    private String associationDate;
    private String createdAt;
}
