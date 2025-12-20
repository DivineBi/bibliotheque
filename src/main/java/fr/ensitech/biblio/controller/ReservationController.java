package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class ReservationController implements IReservationController{

    private final IReservationService reservationService;

    @Override
    @PutMapping("/reserve/{bookId}/{email}")
    public ResponseEntity<?> reserveBook(@PathVariable long bookId, @PathVariable String email) {
        try {
            String message = reservationService.reserveBook(bookId, email);

            if (message.startsWith("Erreur")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    "Erreur lors de la mise à jour du profil"
                            + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
