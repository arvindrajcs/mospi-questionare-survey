package com.mospi.questionare_survey.helperUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class HelperUtils {

    public Pageable getPageable(Long page, Long limit) {

        int pageNo = (page == null || page < 1) ? 1 : page.intValue();
        int pageSize = (limit == null || limit < 1) ? 5 : limit.intValue();

        return PageRequest.of(
                pageNo - 1,
                pageSize,
                Sort.by("menuOrder").ascending() // Or your preferred sort field
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

    public String getClientIP(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                ip = ip.split(",")[0].trim();

                // Convert IPv6 localhost to IPv4 localhost
                if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
                    return "127.0.0.1";
                }
                return ip;
            }
        }
        String ip = request.getRemoteAddr();
        // Convert IPv6 localhost to IPv4 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

}
