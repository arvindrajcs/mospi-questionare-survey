package com.mospi.questionare_survey.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class ApiResponse<T> {

    private T response;       // Actual data
    private boolean status;   // true/false instead of HTTP code
    private String message;   // Message or error description

    // Constructor with data
    public ApiResponse(T response, boolean status, String message) {
        this.response = response;
        this.status = status;
        this.message = message;
    }

    // Constructor without data
    public ApiResponse(boolean status, String message) {
        this.response = null;
        this.status = status;
        this.message = message;
    }

}
