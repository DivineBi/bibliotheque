package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Author;
import fr.ensitech.biblio.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IBookRepository extends JpaRepository<Book, Long> {

    List<Book> findByPublished(boolean published);
    List<Book> findByTitleIgnoreCase(String title);
    List<Book> findByTitleContainingIgnoreCase(String text);
    Book findByIsbnIgnoreCase(String isbn);
    List<Book> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    List<Book> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByIsbn(String isbn);

    List<Book> findByAuthors(Author author) throws Exception;
    List<Book> findByPublicationDate(LocalDate publicationDate);


    //@Query("select b from Book b where  ")
    //List<Book> findBooksByAuthor(Author author);
}
