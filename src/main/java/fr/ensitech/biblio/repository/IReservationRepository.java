package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.entity.Reservation;
import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    // Vérifier si un livre est déjà réservé
    boolean existsByBookIdAndStatusIn(Long bookId, List<ReservationStatus> statuses);

    // Récupérer les réservations par utilisateur
    List<Reservation> findByUserId(Long userId);

    // Récupérer les réservations par livre
    List<Reservation> findByBookId(Long bookId);

    // Filtrer par statut
    List<Reservation> findByStatus(ReservationStatus status);
}
