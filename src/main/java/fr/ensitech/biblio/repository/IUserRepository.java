package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    List<User> findByBirthdateBetween(Date startDate, Date endDate);
    User findByFirstname(String firstName);
    List<User> findByFirstnameAndLastname(String firstName, String lastName);
    List<User> findByBirthdate(Date birthdate);

    Optional<User> findByEmail(String email);

}
