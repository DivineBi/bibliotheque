package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.entity.Reservation;
import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.entity.dto.ReservationCreateDto;
import fr.ensitech.biblio.entity.dto.ReservationDto;
import fr.ensitech.biblio.enums.ReservationStatus;
import fr.ensitech.biblio.repository.IBookRepository;
import fr.ensitech.biblio.repository.IReservationRepository;
import fr.ensitech.biblio.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.EnumSet;
import java.util.List;


@Service
@Transactional
public class ReservationService implements IReservationService {
    private final IReservationRepository reservationRepository;
    private final IUserRepository userRepository;
    private final IBookRepository bookRepository;

    public ReservationService(IReservationRepository reservationRepository,
                              IUserRepository userRepository,
                              IBookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }



    @Override
    public ReservationDto create(Long userId, ReservationCreateDto dto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new Exception("Livre introuvable"));

        // Vérifier si le livre est déjà réservé
        List<ReservationStatus> activeStatuses = List.copyOf(
                EnumSet.of(ReservationStatus.CREATED, ReservationStatus.CONFIRMED)
        );
        if (reservationRepository.existsByBookIdAndStatusIn(book.getId(), activeStatuses)) {
            throw new Exception("Livre déjà réservé.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .status(ReservationStatus.CREATED)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return toDto(saved);
    }



    @Override
    public void cancel(Long reservationId, Long userId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Réservation introuvable"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new Exception("Cette réservation n'appartient pas à cet utilisateur.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.RETURNED
                || reservation.getStatus() == ReservationStatus.EXPIRED) {
            return;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

    }

    @Override
    public void confirm(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Réservation introuvable"));

        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new Exception("Seules les réservations crées peuvent être confirmées.");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    @Override
    public void returnBook(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new Exception("Réservation introuvable"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new Exception("Seules les réservations confirmées peuvent être retournées.");
        }

        reservation.setStatus(ReservationStatus.RETURNED);
        reservationRepository.save(reservation);

    }

    @Override
    public List<ReservationDto> userReservations(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new Exception("Utilisateur introuvable");
        }

        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getBook().getId(),
                reservation.getBook().getTitle(),
                reservation.getUser().getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }
}
