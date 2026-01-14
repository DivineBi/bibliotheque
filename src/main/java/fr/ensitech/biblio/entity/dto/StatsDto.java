package fr.ensitech.biblio.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsDto {
    private Long authorId;
    private String fullname;
    private Integer totalBooks;
    private String categories;
    private String languages;
}
