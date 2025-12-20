package fr.ensitech.biblio.controller;

import org.springframework.http.ResponseEntity;

public interface IReservationController {
    ResponseEntity<?> reserveBook(long bookId, String email);
}
