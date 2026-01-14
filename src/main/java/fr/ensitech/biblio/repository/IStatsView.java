package fr.ensitech.biblio.repository;

public interface IStatsView {
    Long getAuthorId();
    String getFullname();
    Integer getTotalBooks();
    String getCategories();
    String getLanguages();
}
