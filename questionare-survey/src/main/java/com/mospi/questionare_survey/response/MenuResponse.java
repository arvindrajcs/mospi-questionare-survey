package com.mospi.questionare_survey.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class MenuResponse {

    private Long menuId;
    private String parentId;
    private String childId;
    private String menuUrl;
    private String iconName;
    private String labelName;
}
