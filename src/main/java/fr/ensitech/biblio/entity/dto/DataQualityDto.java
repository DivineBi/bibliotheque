package fr.ensitech.biblio.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataQualityDto {
    private String entityType;
    private Long entityId;
    private String label;
    private String issueType;
    private String issueDescription;
    private String severity;
    private String detectedAt;
}
