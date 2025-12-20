package fr.ensitech.biblio.integration;

import fr.ensitech.biblio.entity.Author;
import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.repository.IBookRepository;
import fr.ensitech.biblio.service.BookService;
import fr.ensitech.biblio.utils.Dates;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// Ce test vérifie le bon fonctionnement conjoint
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookServiceIntegrationTest {

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private BookService bookService;

    private Book book;
    private Author author1, author2;

    @SneakyThrows
    @BeforeEach
    void setUp() {

        Author author1 = Author.builder()
                .id(1L)
                .firstname("Pascal")
                .lastname("LAMBERT")
                .birthdate(Dates.convertStringToDate("18/07/1945"))
                .build();

        Author author2 = Author.builder()
                .id(2L)
                .firstname("Benoit")
                .lastname("DECOUX")
                .birthdate(Dates.convertStringToDate("23/11/1938"))
                .build();

        book = Book.builder()
                .id(1L)
                .title("Livre de Java")
                .description("Cours et Exercices en Java")
                .isbn("123456789")
                .editor("Editions Eyrolles")
                .category("Informatique")
                .nbPages((short) 155)
                .language("FR")
                .published(true)
                .publishedDate(Dates.convertStringToDate("15/03/2000"))
                .author(author1)
                .author(author2)
                .build();
    }

    @SneakyThrows
    @Test
    @DisplayName("Ajout d'un livre en BDD")
    void shouldAddBookInDatabase() {
        //GIVEN
        //setup

        //WHEN
        Book savedBook = bookService.addOrUpdateBook(book);

        //THEN
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isGreaterThan(0);
        assertThat(bookRepository.findById(savedBook.getId())).isPresent();
        // Vérifier aussi les auteurs

    }

    @SneakyThrows
    @Test
    @DisplayName("Mise à jour d'un livre dans la BDD")
    void shouldUpdateBookInDatabase() {

        //GIVEN
        Book _book = bookService.addOrUpdateBook(book);
        _book.setNbPages((short)300);
        _book.setLanguage("EN");
        _book.setCategory("Robotique");

        //WHEN
        Book updatedBook =bookService.addOrUpdateBook(_book);

        //THEN
        assertThat(updatedBook.getNbPages()).isEqualTo(300);
        assertThat(updatedBook.getLanguage()).isEqualTo(300);
        assertThat(updatedBook.getCategory()).isEqualTo(300);
    }

    @SneakyThrows
    @Test
    @DisplayName("Mise à jour d'un livre dans la BDD")
    void shouldDeleteBookInDatabase() {

        //GIVEN
        Book _book = bookService.addOrUpdateBook(book);

        //WHEN
        bookService.deleteBook(_book.getId());

        //THEN
        Book deletedBook = bookService.getBook(book.getId());
        assertThat(deletedBook).isNull();
    }



}
