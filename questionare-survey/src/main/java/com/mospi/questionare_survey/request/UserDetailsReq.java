package com.mospi.questionare_survey.request;

import lombok.Data;


import java.sql.Timestamp;

@Data
public class UserDetailsReq {
    private String empCode;
    private String empType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String bloodGroup;
    private String maritalStatus;
    private String designation;
    private String ctc;
    private Number contactNo;
    private Number emgContactNo;
    private String email;
    private String shiftType;
    private Timestamp joinDate;
    private Timestamp probationDate;
    private Timestamp resgnDate;
    private String reasonForLeaving;
    private Timestamp lwd;

}
