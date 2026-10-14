package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mospi.questionare_survey.entity.MasDivision;
import com.mospi.questionare_survey.entity.MasScheduleType;
import com.mospi.questionare_survey.entity.MasSurveyType;
import com.mospi.questionare_survey.response.MenuResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateSurveyReq {

    private String title;
    private Long surveyTypeId;
    private Long divisionId;
    private String description;
    private String templateName;

    private Long scheduleTypeId;

    private List<BlockDetailsReq> blockDetailsList;

}
