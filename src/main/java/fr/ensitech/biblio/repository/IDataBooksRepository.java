package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDataBooksRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM v_databooks", nativeQuery = true)
    List<IDataBooksView> findAllDataBooks();
}
