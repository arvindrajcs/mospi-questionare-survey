package com.mospi.questionare_survey.helperUtil;

import lombok.Getter;



public enum ErrorCode {



    AU1000("AU1000", "Invalid Username or Password"),

    AU1001("AU1001", "Invalid User , User Not Found");


    @Getter

    private final String code;

    private final String message;



    ErrorCode(String code, String message){

        this.code = code;

        this.message = message;

    }



    public String getFinalMessage(){

        return message;

    }

}
