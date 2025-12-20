package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.Author;
import fr.ensitech.biblio.entity.Book;
import fr.ensitech.biblio.repository.IAuthorRepository;
import fr.ensitech.biblio.repository.IBookRepository;
import fr.ensitech.biblio.utils.Dates;
import jakarta.el.ELException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookService implements IBookService {

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private IAuthorRepository authorRepository;

    @Override
    public Book addOrUpdateBook(Book book) throws Exception {

        // Vérification de l'ID
        if (book.getId() < 0) {
            throw new Exception("Book id must be greater than 0 !");
        }

        // Cas 1: Ajout de livre
        if (book.getId() == 0) {

            // Vérifier si un livre avec le même ISBN existe déjà
            Book _book = bookRepository.findByIsbnIgnoreCase(book.getIsbn());
            if (_book != null) {
                throw new IllegalArgumentException("Book with this ISBN already exists");
            }

            // Validation des auteurs
            Set<Author> validatedAuthors = validateAuthors(book.getAuthors());

            book.setAuthors(validatedAuthors);

            //Sauvegarde d'un nouveau livre
            return bookRepository.save(book);
        }

        // Cas 2: mise à jour de livre
            Book _book = bookRepository.findById(book.getId()).orElse(null);
            if (_book == null) {
                throw new Exception("Book not found");
            }

            // validation des auteurs
            Set<Author> validatedAuthors = validateAuthors(book.getAuthors());

            // Vérifier si l'ISBN existe déjà pour un autre livre
            Book isbncheck = bookRepository.findByIsbnIgnoreCase(book.getIsbn());
            if (isbncheck != null && isbncheck.getId() != book.getId()) {
                throw new IllegalArgumentException("ISBN already exists!");
            }

            // Mise à jour des  champs
            _book.setIsbn(book.getIsbn());
            _book.setTitle(book.getTitle());
            _book.setDescription(book.getDescription());
            _book.setEditor(book.getEditor());
            _book.setPublishedDate(book.getPublishedDate());
            _book.setCategory(book.getCategory());
            _book.setLanguage(book.getLanguage());
            _book.setNbPages(book.getNbPages());
            _book.setPublished(book.isPublished());
            _book.setAuthors(validatedAuthors);
            return bookRepository.save(_book);

    }


    // Validation et chargement des auteurs
    private Set<Author> validateAuthors(Set<Author> authors) throws Exception {
        if (authors == null) return new HashSet<>();

        Set<Author> validated =new HashSet<>();
        for (Author a : authors) {
            if (a == null || a.getId() == 0) {
                throw new IllegalArgumentException("Author id must not be null");
            }
            Author existingAuthor = authorRepository.findById(a.getId())
                    .orElseThrow(() -> new ELException("Author not found with id: " + a.getId()));
            validated.add(existingAuthor);
        }
        return validated;
    }

    @Override
    public void deleteBook(long id) throws Exception {

        //bookRepository.delete(book)
        if (id <= 0) {
            throw new IllegalArgumentException("id must be > 0 !");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found !"));

        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> getBooks() throws Exception {
        return bookRepository.findAll();
    }

    @Override
    public Book getBook(long id) throws Exception {
        Optional<Book> optional = bookRepository.findById(id);
        //return optional.get();
        return optional.orElse(null);
    }

    @Override
    public List<Book> getBooksByTitle(String title) throws Exception {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> getBooksByAuthor(Author author) throws Exception {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public List<Book> getBooksBetweenYears(int startYear, int endYear) throws Exception {
        //Calendar startCalendar = Calendar.getInstance();
        //startCalendar.set(Calendar.YEAR, startYear);
        //startCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        //startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        //Date startDate = startCalendar.getTime();

        //Calendar endCalendar = Calendar.getInstance();
        //endCalendar.set(Calendar.YEAR, endYear);
        //endCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
        //endCalendar.set(Calendar.DAY_OF_MONTH, 31);
        //Date endDate = endCalendar.getTime();

        Date startDate = Dates.convertStringToDate("01/01/" + startYear);
        Date endDate = Dates.convertStringToDate("31/12/" + endYear);

        return bookRepository.findByPublishedDateBetween(startDate, endDate);
    }

    @Override
    public List<Book> getBooksByPublished(boolean published) {
        return bookRepository.findByPublished(published);
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbnIgnoreCase(isbn);
    }

    @Override
    public List<Book> getBooksByTitleOrDescription(String title, String description) {
        return bookRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title, description);
    }

    @Override
    public List<Book> findByAuthor(Author author) throws Exception{
        return bookRepository.findByAuthor(author);
    }


}
