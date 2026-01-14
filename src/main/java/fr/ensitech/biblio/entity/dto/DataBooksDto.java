package fr.ensitech.biblio.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataBooksDto {

    private Long bookId;
    private String title;
    private String category;
    private String language;
    private String authors;
    private Integer authorsCount;
}
