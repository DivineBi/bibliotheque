package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.entity.Reservation;
import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.repository.IBookRepository;
import fr.ensitech.biblio.repository.IReservationRepository;
import fr.ensitech.biblio.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReservationService implements IReservationService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Override
    public String reserveBook(long bookId, String email) throws Exception {

        // Check user
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "Erreur : utilisateur introuvable.";
        }
        User user = optionalUser.get();

        // check book
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            return "Erreur: livre introuvable.";
        }
        Book book = optionalBook.get();

        // User cannot reserve more than 3 books
        int reservationsUser = reservationRepository.countByUser(user);
        if (reservationsUser >= 3) {
            return "Erreur : limite de 3 réservations atteinte.";
        }

        // User cannot reserve twice the same book
        if (reservationRepository.existsByUserAndBook(user, book)) {
            return "Erreur: vous avez déjà réservé ce livre.";
        }

        // Check stock availability
        int reservationsBook = reservationRepository.countByBook(book);
        if (reservationsBook >= book.getQuantity()) {
            return "Erreur : toutes les copies disponibles ont déjà été réservées.";
        }

        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        return "Réservation effectuée avec succès.";
    }
}
