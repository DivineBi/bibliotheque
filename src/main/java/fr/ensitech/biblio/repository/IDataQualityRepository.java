package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDataQualityRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT * FROM v_data_quality_library", nativeQuery = true)
    List<IDataQualityView> findAllIssues();
}
