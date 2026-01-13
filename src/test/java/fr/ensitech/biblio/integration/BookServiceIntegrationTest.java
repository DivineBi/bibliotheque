package fr.ensitech.biblio.integration;

import fr.ensitech.biblio.entity.Author;
import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.repository.IAuthorRepository;
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

import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.contains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


// Ce test vérifie le bon fonctionnement conjoint
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookServiceIntegrationTest {

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private IAuthorRepository authorRepository;

    private Book book;
    private Author author1, author2;

    @SneakyThrows
    @BeforeEach
    void setUp() {

        author1 = Author.builder()
                .firstname("Pascal")
                .lastname("LAMBERT")
                .birthdate(Dates.convertStringToDate("18/07/1945"))
                .build();

        author2 = Author.builder()
                .firstname("Benoit")
                .lastname("DECOUX")
                .birthdate(Dates.convertStringToDate("23/11/1938"))
                .build();

        // save authors
        author1 = authorRepository.save(author1);
        author2 = authorRepository.save(author2);

        book = Book.builder()
                .title("Livre de Java")
                .description("Cours et Exercices en Java")
                .isbn("123456789")
                .editor("Editions Eyrolles")
                .category("Informatique")
                .nbPages((short) 155)
                .language("FR")
                .published(true)
                .publishedDate(Dates.convertStringToDate("15/03/2000"))
                .authors(Set.of(author1, author2))
                .build();
    }

    @SneakyThrows
    @Test
    @DisplayName("Ajout d'un livre en BDD")
    void shouldAddBookInDatabase() {

        Book savedBook = bookService.addOrUpdateBook(book);

        //THEN
        assertThat(savedBook.getId()).isNotNull();
        assertThat(bookRepository.findById(savedBook.getId())).isPresent();
        assertThat(savedBook.getAuthors().size()).isEqualTo(2);

    }

    @SneakyThrows
    @Test
    @DisplayName("Mise à jour d'un livre dans la BDD")
    void shouldUpdateBookInDatabase() {

        //GIVEN
        Book _book = bookService.addOrUpdateBook(book);
        _book.setNbPages(300);
        _book.setLanguage("EN");
        _book.setCategory("Robotique");

        //WHEN
        Book updatedBook =bookService.addOrUpdateBook(_book);

        //THEN
        assertThat(updatedBook.getNbPages()).isEqualTo(300);
        assertThat(updatedBook.getLanguage()).isEqualTo("EN");
        assertThat(updatedBook.getCategory()).isEqualTo("Robotique");
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
        assertThat(bookService.getBook(_book.getId())).isNull();
    }

    @SneakyThrows
    @Test
    @DisplayName("Recherche de livres par titre")
    void shouldFindBooksByTitle() {
        // GIVEN
        Book _book = bookService.addOrUpdateBook(book);

        // WHEN
        List<Book> results = bookService.getBooksByTitle("Java");

        // THEN
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).contains("Java");
    }

    @Test
    @DisplayName("Recherche de livres par auteur")
    @SneakyThrows
    void shouldFindBooksByAuthor() {
        // GIVEN
        bookService.addOrUpdateBook(book);

        // WHEN
        var results = bookService.getBooksByAuthor(author1);

        // THEN
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getAuthors()).contains(author1);
    }

    @Test
    @DisplayName("Recherche de livres entre deux années")
    @SneakyThrows
    void shouldFindBookBetweenYears() {
        // GIVEN
        bookService.addOrUpdateBook(book);

        // WHEN
        var results = bookService.getBooksBetweenYears(1990, 2020);

        // THEN
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Erreur : ISBN déjà existant")
    @SneakyThrows
    void shouldThrowWhenIsbnAlreadyExists() {
        // GIVEN
        bookService.addOrUpdateBook(book);

        Book duplicate = Book.builder()
                .title("Autre livre")
                .isbn(book.getIsbn()) // même ISBN
                .authors(Set.of(author1))
                .build();

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.addOrUpdateBook(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN");
    }


    @Test
    @DisplayName("Erreur : mise à jour d'un livre inexistant")
    @SneakyThrows
    void shouldThrowWhenUpdatingNonExistingBook() {
        Book nonExisting = Book.builder()
                .id(99L)
                .isbn("12345")
                .title("Livre")
                .authors(Set.of(author1))
                .build();

        assertThatThrownBy(() -> bookService.addOrUpdateBook(nonExisting))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Book not found");
    }


    @Test
    @DisplayName("Erreur : auteur inexistant")
    @SneakyThrows
    void shouldThrowWhenAuthorDoesNotExist() {
        Author unkownAuthor = Author.builder().id(999L).build();

        Book invalidBook = Book.builder()
                .title("Livre")
                .isbn("12345")
                .authors(Set.of(unkownAuthor))
                .build();

        assertThatThrownBy(() -> bookService.addOrUpdateBook(invalidBook))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Author not found");
    }


    @Test
    @DisplayName("Erreur : suppression d'un livre inexistant")
    @SneakyThrows
    void shouldThrowWhenDeletingNonExistingBook() {
        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found");
    }


    @Test
    @DisplayName("Récupération d'un livre par ID")
    @SneakyThrows
    void shouldGetBookById() {
        Book saved = bookService.addOrUpdateBook(book);

        Book found = bookService.getBook(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
    }


    @Test
    @DisplayName("Récupération de tous les livres")
    @SneakyThrows
    void shouldGetAllBooks() {
        bookService.addOrUpdateBook(book);

        var list = bookService.getBooks();

        assertThat(list).isNotEmpty();
    }

}
