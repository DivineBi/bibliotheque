package fr.ensitech.biblio.service;

public interface IReservationService {
    String reserveBook(long bookId, String email) throws Exception;
}
