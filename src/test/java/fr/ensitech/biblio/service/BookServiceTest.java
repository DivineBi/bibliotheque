package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.Author;
import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.repository.IAuthorRepository;
import fr.ensitech.biblio.repository.IBookRepository;
import fr.ensitech.biblio.utils.Dates;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IAuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Author author1;
    private Author author2;

    @SneakyThrows
    @BeforeEach
    void setUp() {

        author1 = Author.builder()
                .id(1L)
                .firstname("Pascal")
                .lastname("LAMBERT")
                .birthdate(Dates.convertStringToDate("18/07/1945"))
                .build();

        author2 = Author.builder()
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
                .authors(new ArrayList<>(List.of(author1, author2)))
                .build();
    }

    @AfterEach
    void tearDown() {

    }

    @SneakyThrows
    @Test
    @DisplayName("Ajouter un livre valide avec plusieurs auteurs")
    void shouldAddBookSuccessfully() {
        // bouchonne le repository pour ne pas tester la base des données
        when(bookRepository.save(book)).thenReturn(book);
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(author2));

        //WHEN
        Book savedBook = bookService.addOrUpdateBook(book);


        //THEN
        assertThat(savedBook)
                .isNotNull()
                .extracting(Book::getTitle, Book::getDescription)
                .containsExactly("Livre de Java", "Cours et Exercices en Java");

        assertThat(savedBook.getAuthors())
                .hasSize(2)
                .extracting(Author::getFirstname)
                .containsExactlyInAnyOrder("Pascal", "Benoit");

        assertThat(savedBook.getAuthors())
                .hasSize(2)
                .extracting(Author::getLastname)
                .containsExactlyInAnyOrder("LAMBERT", "DECOUX");
    verify(bookRepository).save(book);
    }

    @SneakyThrows
    @Test
    void shouldThrowExceptionWhenSavingBookWithAlreadyExistIsbn() {
        //GIVEN
        book.setId(1L); // id différent de 0 pour aller dans la branche update

        Book otherBook = Book.builder()
                        .id(99L)
                        .isbn("123456789")
                        .title("Autre livre")
                        .build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(author2));
        when(bookRepository.findByIsbnIgnoreCase("123456789")).thenReturn(otherBook);

        //WHEN + THEN
        assertThatThrownBy(
                () -> bookService.addOrUpdateBook(book))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN already exists!");

    }

    @SneakyThrows
    @Test
    void shouldDeleteExistingBook() {
        //GIVEN
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book));

        //WHEN
        bookService.deleteBook(1L);

        //THEN
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionWhenDeletingNoExistingBook() {
        //GIVEN
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());


        //THEN
        assertThatThrownBy(
                () -> bookService.deleteBook(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be > 0 !");

        assertThatThrownBy(
                () -> bookService.deleteBook(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be > 0 !");

        assertThatThrownBy(
                () -> bookService.deleteBook(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found !");

    }

    @SneakyThrows
    @Test
    void shouldUpdatingExistingBook() {
        //GIVEN
        Book updatedBook = Book.builder()
                .id(1L)
                .title("Nouveau titre")
                .description("Nouvelle description")
                .isbn("123456789") // même ISBN
                .editor("Editions Eyrolles")
                .category("Informatique")
                .nbPages((short) 200)
                .language("FR")
                .published(true)
                .publishedDate(Dates.convertStringToDate("15/03/2001"))
                .authors(new HashSet<>(Arrays.asList(author1, author2)))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbnIgnoreCase("123456789")).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author1));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(author2));


        // WHEN
        Book result = bookService.addOrUpdateBook(updatedBook);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Nouveau titre");
        assertThat(result.getDescription()).isEqualTo("Nouvelle description");

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNoExistingBook() {
        // GIVEN
        Book updatedBook = Book.builder()
                .id(99L) // n'existe pas
                .title("Titre")
                .description("Desc")
                .isbn("999999999")
                .editor("Editeur")
                .category("Cat")
                .nbPages((short) 100)
                .language("FR")
                .published(true)
                .publishedDate(new Date())
                .authors(new HashSet<>(Arrays.asList(author1)))
                .build();

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.addOrUpdateBook(updatedBook))
                .isInstanceOf(Exception.class)
                .hasMessage("Book not found");

        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).save(any(Book.class));
    }


    @SneakyThrows
    @Test
    void shouldFindBooksByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase("Java")).thenReturn(List.of(book));

        List<Book> books = bookService.getBooksByTitle("Java");

        assertThat(books).hasSize(1).contains(book);
        verify(bookRepository).findByTitleContainingIgnoreCase("Java");
    }

    @Test
    @SneakyThrows
    void shouldGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookService.getBooks();

        assertThat(books).hasSize(1).contains(book);
        verify(bookRepository).findAll();
    }

    @Test
    @SneakyThrows
    void shouldGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBook(1L);

        assertThat(foundBook).isNotNull().isEqualTo(book);
        verify(bookRepository).findById(1L);
    }

    @Test
    @SneakyThrows
    void shouldFindBooksByAuthor() {
        when(bookRepository.findByAuthors(author1)).thenReturn(List.of(book));

        List<Book> books = bookService.getBooksByAuthor(author1);
        verify(bookRepository).findByAuthors(author1);
    }

    @Test
    @SneakyThrows
    void shouldFindBooksBetweenYears() {
        // GIVEN
        int startYear = 1999;
        int endYear = 2001;

        Date startDate = Dates.convertStringToDate("01/01/" + startYear);
        Date endDate = Dates.convertStringToDate("31/12/" + endYear);

        when(bookRepository.findByPublishedDateBetween(startDate, endDate))
                .thenReturn(List.of(book));

        // WHEN
        List<Book> books = bookService.getBooksBetweenYears(1999,2001);

        // THEN
        assertThat(books).hasSize(1).contains(book);
        verify(bookRepository).findByPublishedDateBetween(startDate,endDate);
    }

    @Test
    void shouldFindBooksByPublished() {
        when(bookRepository.findByPublished(true)).thenReturn(List.of(book));

        List<Book> books = bookService.getBooksByPublished(true);

        assertThat(books).hasSize(1).contains(book);
        verify(bookRepository).findByPublished(true);
    }

    @Test
    void shouldFindBookByIsbn() {
        when(bookRepository.findByIsbnIgnoreCase("123456789")).thenReturn(book);

        Book foundBook = bookService.getBookByIsbn("123456789");

        assertThat(foundBook).isNotNull().isEqualTo(book);
        verify(bookRepository).findByIsbnIgnoreCase("123456789");
    }

    @Test
    void shouldFindBooksByTitleOrDescription() {
        when(bookRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Java", "Exercices"))
                .thenReturn(List.of(book));

        List<Book> books = bookService.getBooksByTitleOrDescription("Java", "Exercices");

        assertThat(books).hasSize(1).contains(book);

        verify(bookRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Java", "Exercices");
    }
}
