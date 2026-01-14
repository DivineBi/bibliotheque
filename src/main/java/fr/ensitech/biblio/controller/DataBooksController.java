package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.repository.IDataBooksRepository;
import fr.ensitech.biblio.repository.IDataBooksView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class DataBooksController {

    private final IDataBooksRepository repository;

    private DataBooksController(IDataBooksRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/databooks")
    public List<IDataBooksView> getDataBooks() {
        return repository.findAllDataBooks();
    }
}
