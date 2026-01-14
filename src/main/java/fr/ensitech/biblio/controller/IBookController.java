package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.Book;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IBookController {

    ResponseEntity<Book> createBook(Book book);
    ResponseEntity<Book> getBookById(Long id);
    ResponseEntity<Book> updateBook(Book book);
    ResponseEntity<String> deleteBook(Long id);
    ResponseEntity<List<Book>> getAllBooks();
    ResponseEntity<List<Book>> getBooksByPublicationDate(LocalDate publicationDate);
}
