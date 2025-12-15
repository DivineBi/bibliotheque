package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.PasswordHistory;
import fr.ensitech.biblio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findTop5ByUserOrderByChangedAtDesc(User user);
    List<PasswordHistory> findByUserOrderByChangedAtDesc(User user);
}
