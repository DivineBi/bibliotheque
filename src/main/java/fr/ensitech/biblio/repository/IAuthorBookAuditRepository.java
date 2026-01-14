package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.AuthorBookAudit;
import fr.ensitech.biblio.entity.dto.AuthorBookAuditDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAuthorBookAuditRepository extends JpaRepository<AuthorBookAudit, Long> {

    List<AuthorBookAudit> findByAuthorId(Long authorId);
    List<AuthorBookAudit> findByBookId(Long bookId);
    List<AuthorBookAudit> findByAuthorIdAndBookId(Long authorId, Long bookId);
}
