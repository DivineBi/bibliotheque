package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.Reservation;
import fr.ensitech.biblio.entity.dto.ReservationCreateDto;
import fr.ensitech.biblio.entity.dto.ReservationDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReservationController {
    ResponseEntity<ReservationDto> create(Long userId, ReservationCreateDto dto);
    ResponseEntity<Void> cancel(Long id, Long userId);
    ResponseEntity<Void> confirm(Long id);
    ResponseEntity<Void> returnBook(Long id);
    List<ReservationDto> userReservations(Long userId);
}
