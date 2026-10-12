package com.mospi.questionare_survey.helperUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseUtils {

    // 1. Simple Success Response
    public static <T> ApiResponse<T> createSuccessResponse(T data) {
        return new ApiResponse<>(
                data,
                true,   // status = true
                "success"
        );
    }

    // 2. Pageable Success Response
    public static <T> ApiResponseWithPageable<T> createPageableSuccessResponse(
            T data,
            Long listSize,
            Integer totalPages,
            Integer currentPage,
            Integer pageSize
    ) {
        return new ApiResponseWithPageable<>(
                data,
                true,   // status = true
                "success",
                listSize,
                totalPages,
                currentPage,
                pageSize
        );
    }

    // 3. Optional method using TypeReference
    public static <T> ApiResponse<T> createSuccessResponse(
            T data,
            TypeReference<T> typeReference
    ) {
        return createSuccessResponse(data);
    }

    // 4. Custom Cred Response
    public static <T> ApiResponse<T> createSuccessResponseWithCred(
            T data, TypeReference<T> typeReference
    ) {
        return new ApiResponse<>(
                data,
                true,   // status = true
                "success"
        );
    }

    // 5. Failure Response
    public static <T> ApiResponse<T> createFailureResponse(String msg) {
        return new ApiResponse<>(
                null,
                false,   // status = false
                msg
        );
    }
}