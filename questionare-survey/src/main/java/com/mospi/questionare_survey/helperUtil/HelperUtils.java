package com.mospi.questionare_survey.helperUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.repository.QuestionDetailsRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HelperUtils {


    private static final Logger logger = LoggerFactory.getLogger(HelperUtils.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QuestionDetailsRepo questionDetailsRepo;

    public void generateResponseTableForSurvey(Long surveyId, String surveyCode) {
        if (surveyCode == null || surveyCode.trim().isEmpty()) {
            logger.error("Survey code is null or empty for surveyId: {}", surveyId);
            throw new SDDException(false, "Survey code cannot be empty for dynamic table creation");
        }
        List<String> questionCodes = questionDetailsRepo.findQuestionCodesBySurveyId(surveyId);

        if (questionCodes == null || questionCodes.isEmpty()) {
            logger.warn("No question codes found for surveyId: {} to create dynamic table.", surveyId);
            return;
        }
        String cleanedSurveyCode = surveyCode.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        String tableName = "fact_" + cleanedSurveyCode;

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        sql.append("tableName_id SERIAL PRIMARY KEY, ");
        sql.append("survey_id SERIAL PRIMARY KEY, ");
        sql.append("createdBy BIGINT NOT NULL, ");
        sql.append("createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");

        for (int i = 0; i < questionCodes.size(); i++) {
            String colName = questionCodes.get(i).replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
            sql.append(colName).append(" VARCHAR(255)");

            if (i < questionCodes.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(");");
        try {
            jdbcTemplate.execute(sql.toString());
            logger.info("Dynamic response table '{}' created successfully for surveyId: {}", tableName, surveyId);
            System.out.println("Dynamic response table created successfully for surveyId ");
        } catch (Exception e) {
            logger.error("Failed to create dynamic table '{}' for surveyId: {}", tableName, surveyId, e);
            throw new SDDException(false, "Failed to create dynamic response table in database");
        }
    }



    public Pageable getPageable(Long page, Long limit) {
        int pageNo = (page == null || page < 1) ? 1 : page.intValue();
        int pageSize = (limit == null || limit < 1) ? 5 : limit.intValue();

        return PageRequest.of(
                pageNo - 1,
                pageSize,
                Sort.by("createdAt").descending() // Safe default property present across entities
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
