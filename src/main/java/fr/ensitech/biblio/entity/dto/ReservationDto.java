package fr.ensitech.biblio.entity.dto;

import fr.ensitech.biblio.enums.ReservationStatus;

import java.time.LocalDate;

public record ReservationDto (
    Long id,
    Long bookId,
    String bookTitle,
    Long userId,
    LocalDate startDate,
    LocalDate endDate,
    ReservationStatus status
    ) {}
