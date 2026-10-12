package com.mospi.questionare_survey.exception;

import java.sql.SQLException;
import java.util.Locale;

final class SafeErrorMessage {

    private static final String GENERIC_MESSAGE =
            "A system error occurred while processing the request.";

    private SafeErrorMessage() {
    }

    static String from(Throwable throwable) {
        return from(throwable, GENERIC_MESSAGE);
    }

    static String from(Throwable throwable, String fallbackMessage) {
        if (throwable == null) {
            return fallbackMessage;
        }

        if (containsSensitiveBackendDetails(throwable)) {
            return fallbackMessage;
        }

        String message = throwable.getMessage();

        return message == null || message.isBlank()
                ? fallbackMessage
                : from(message, fallbackMessage);
    }

    static String from(String message) {
        return from(message, GENERIC_MESSAGE);
    }

    static String from(String message, String fallbackMessage) {
        if (message == null || message.isBlank()) {
            return fallbackMessage;
        }

        return looksSensitive(message)
                ? fallbackMessage
                : message;
    }

    static String withPrefix(String prefix, Throwable throwable) {
        return withPrefix(prefix, throwable, GENERIC_MESSAGE);
    }

    static String withPrefix(
            String prefix,
            Throwable throwable,
            String fallbackMessage) {

        String safeMessage = from(throwable, fallbackMessage);

        return (prefix == null ? "" : prefix) + safeMessage;
    }

    private static boolean containsSensitiveBackendDetails(
            Throwable throwable) {

        Throwable current = throwable;

        // Prevent infinite loops if a cause chain is malformed.
        int depth = 0;
        while (current != null && depth++ < 100) {

            if (current instanceof SQLException) {
                return true;
            }

            String className = current.getClass()
                    .getName()
                    .toLowerCase(Locale.ROOT);

            if (className.contains("hibernate")
                    || className.contains("jdbc")
                    || className.contains("jpa")
                    || className.contains("persistence")
                    || className.contains("dataaccess")) {
                return true;
            }

            if (looksSensitive(current.getMessage())) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private static boolean looksSensitive(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }

        String lower = message.toLowerCase(Locale.ROOT);

        return lower.contains("ora-")
                || lower.contains("sqlstate")
                || lower.contains("sql state")
                || lower.contains("sqlexception")
                || lower.contains("sqlgrammar")
                || lower.contains("jdbc")
                || lower.contains("hibernate")
                || lower.contains("constraint [")
                || lower.contains("could not execute statement")
                || lower.contains("could not extract resultset")
                || lower.contains("could not prepare statement")
                || lower.contains("preparedstatement")
                || lower.contains("select ")
                || lower.contains("insert ")
                || lower.contains("update ")
                || lower.contains("delete ")
                || lower.contains(" from ")
                || lower.contains(" where ")
                || lower.contains("com.rshaa")
                || lower.contains("nhcx.rshaa");
    }
}