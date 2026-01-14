package fr.ensitech.biblio.repository;

import fr.ensitech.biblio.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStatsRepository extends JpaRepository<Author, Long> {

    @Query(value = "SELECT * FROM v_stats_authors", nativeQuery = true)
    List<IStatsView> findAllStats();
}
