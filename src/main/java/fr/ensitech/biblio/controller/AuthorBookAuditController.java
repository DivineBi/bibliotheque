package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.AuthorBookAudit;
import fr.ensitech.biblio.entity.dto.AuthorBookAuditDto;
import fr.ensitech.biblio.repository.IAuthorBookAuditRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
public class AuthorBookAuditController {

    private final IAuthorBookAuditRepository repository;

    public AuthorBookAuditController(IAuthorBookAuditRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AuthorBookAuditDto> getAudit(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long bookId
    ) {
        List<AuthorBookAudit> audits;

        if (authorId != null && bookId != null) {
            audits = repository.findByAuthorIdAndBookId(authorId, bookId);
        } else if (authorId != null) {
            audits = repository.findByAuthorId(authorId);
        } else if (bookId != null) {
            audits = repository.findByBookId(bookId);
        } else {
            audits = repository.findAll();
        }
        return audits.stream()
                .map(this::toDto)
                .toList();
    }

    private AuthorBookAuditDto toDto(AuthorBookAudit entity) {
        AuthorBookAuditDto dto = new AuthorBookAuditDto();
        dto.setId(entity.getId());
        dto.setAuthorId(entity.getAuthorId());
        dto.setBookId(entity.getBookId());
        dto.setOperationType(entity.getOperationType());
        dto.setAssociationDate(entity.getAssociationDate());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
