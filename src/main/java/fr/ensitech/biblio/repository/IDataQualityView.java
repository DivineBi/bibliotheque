package fr.ensitech.biblio.repository;

public interface IDataQualityView {
    String getEntityType();
    Long getEntityId();
    String getLabel();
    String getIssueType();
    String getIssueDescription();
    String getSeverity();
    String getDetectedAt();
}
