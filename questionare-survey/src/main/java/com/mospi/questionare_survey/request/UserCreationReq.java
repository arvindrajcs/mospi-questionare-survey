package com.mospi.questionare_survey.request;

import lombok.Data;

@Data
public class UserCreationReq {
        private String empCode;
        private String name;
        private String email;
        private String mobileNo;
        private String curPassword;
        private String roleId;
}
