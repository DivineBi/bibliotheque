package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.repository.IStatsRepository;
import fr.ensitech.biblio.repository.IStatsView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class StatsController {

    private final IStatsRepository repository;

    public StatsController(IStatsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/stats")
    public List<IStatsView> getStats() {
        return repository.findAllStats();
    }
}
