package fr.ensitech.biblio.repository;

public interface IDataBooksView {
    Long getBookId();
    String getTitle();
    String getCategory();
    String getLanguage();
    String getAuthors();
    Integer getAuthorsCount();
}
