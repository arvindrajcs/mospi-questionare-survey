package com.mospi.questionare_survey.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseWithPageable<T> extends ApiResponse<T> {

    private Long listSize;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;

    public ApiResponseWithPageable(
            T response,
            boolean status,
            String message,
            Long listSize,
            Integer totalPages,
            Integer currentPage,
            Integer pageSize
    ) {
        super(response, status, message);
        this.listSize = listSize;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
}