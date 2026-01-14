package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.repository.IDataBooksRepository;
import fr.ensitech.biblio.repository.IDataQualityRepository;
import fr.ensitech.biblio.repository.IDataQualityView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class DataQualityController {

    private final IDataQualityRepository repository;

    public DataQualityController(IDataQualityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/data-quality")
    public List<IDataQualityView> getDataQuality() {
        return repository.findAllIssues();
    }
}
