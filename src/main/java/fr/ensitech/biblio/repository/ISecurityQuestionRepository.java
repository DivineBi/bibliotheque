package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {

}
