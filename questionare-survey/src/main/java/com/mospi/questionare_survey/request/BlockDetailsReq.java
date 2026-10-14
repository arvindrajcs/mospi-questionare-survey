package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class BlockDetailsReq {
    private String blockHeading;
    private Integer displayOrder;
    private String fieldsData;
    private List<QuestinDetailsReq> questionDetailsList; // Nested questions for this block
}
