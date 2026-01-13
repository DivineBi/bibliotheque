package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.dto.ReservationCreateDto;
import fr.ensitech.biblio.entity.dto.ReservationDto;

import java.util.List;

public interface IReservationService {
    ReservationDto create(Long userId, ReservationCreateDto dto) throws Exception;
    void cancel(Long reservationId, Long userId) throws Exception;
    void confirm(Long reservationId) throws Exception;
    void returnBook(Long reservationId) throws Exception;
    List<ReservationDto> userReservations(Long userId) throws Exception;
}
