package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.entity.Reservation;
import fr.ensitech.biblio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    int countByUser(User user);
    boolean existsByUserAndBook(User user, Book book);
    int countByBook(Book book);
}
