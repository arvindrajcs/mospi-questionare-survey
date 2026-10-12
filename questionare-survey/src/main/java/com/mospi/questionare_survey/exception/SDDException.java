package com.mospi.questionare_survey.exception;

import com.mospi.questionare_survey.helperUtil.ErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class SDDException extends RuntimeException {

    private final String field;
    private final boolean status;
    private final Map<String, Object> response;

    // Existing constructors

    public SDDException(String field, boolean status, String message) {
        super(message);
        this.field = field;
        this.status = status;
        this.response = null;
    }

    public SDDException(boolean status, String message) {
        super(message);
        this.field = null;
        this.status = status;
        this.response = null;
    }

    public SDDException(
            boolean status, String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.status = status;
        this.response = null;
    }

    public SDDException(
            boolean status,
            String message,
            Map<String, Object> response) {
        super(message);
        this.field = null;
        this.status = status;
        this.response = response == null ? null : Map.copyOf(response);
    }

    // ErrorCode constructors

    // Default status = true (preserves existing behavior)
    public SDDException(ErrorCode errorCode) {
        super(errorCode.getFinalMessage());
        this.field = null;
        this.status = true;
        this.response = Map.of("errorCode", errorCode.getCode());
    }

    public SDDException(boolean status, ErrorCode errorCode) {
        super(errorCode.getFinalMessage());
        this.field = null;
        this.status = status;
        this.response = Map.of("errorCode", errorCode.getCode());
    }

    public SDDException(
            String field, boolean status, ErrorCode errorCode) {
        super(errorCode.getFinalMessage());
        this.field = field;
        this.status = status;
        this.response = Map.of("errorCode", errorCode.getCode());
    }
}
