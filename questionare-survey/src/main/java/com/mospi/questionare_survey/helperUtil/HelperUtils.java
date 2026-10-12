package com.mospi.questionare_survey.helperUtil;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.awt.print.Pageable;

public class HelperUtils {


    public Pageable getPageable(Long page, Long limit) {

        int pageNo = (page == null || page < 1) ? 1 : page.intValue();
        int pageSize = (limit == null || limit < 1) ? 5 : limit.intValue();

        return PageRequest.of(
                pageNo - 1,
                pageSize,
                Sort.by("createdDate").descending()
        );
    }


    public Pageable getPageable(Long page, Long limit, Sort sort) {

        int pageNo = (page == null || page < 1) ? 1 : page.intValue();
        int pageSize = (limit == null || limit < 1) ? 5 : limit.intValue();

        return PageRequest.of(
                pageNo - 1,
                pageSize,
                sort
        );
    }
}
