package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class QuestinDetailsReq {
    private Long surveyId;
    private Long blockId;
    private String questionNumber;
    private String fieldInstructions;
    private String questionHeading;
    private Integer displayOrder;
    private boolean isMandatory;
}
