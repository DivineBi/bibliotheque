package fr.ensitech.biblio.entity.dto;

import java.time.LocalDate;

public record ReservationCreateDto (
    Long bookId,
    LocalDate startDate,
    LocalDate endDate,
    String comment
    ) {}
