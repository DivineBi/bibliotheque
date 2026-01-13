package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.dto.ReservationCreateDto;
import fr.ensitech.biblio.entity.dto.ReservationDto;
import fr.ensitech.biblio.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController implements IReservationController{

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    @PostMapping("/users/{userId}")
    public ResponseEntity<ReservationDto> create(@PathVariable Long userId,
                                                 @RequestBody ReservationCreateDto dto) {
        try {
            ReservationDto created = reservationService.create(userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       @RequestParam Long userId) {
        try {
            reservationService.cancel(id, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        try {
            reservationService.confirm(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PostMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        try {
            reservationService.returnBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @GetMapping("/users/{userId}")
    public List<ReservationDto> userReservations(@PathVariable Long userId) {
        try {
            return reservationService.userReservations(userId);
        } catch (Exception e) {
            return List.of();
        }
    }
}
