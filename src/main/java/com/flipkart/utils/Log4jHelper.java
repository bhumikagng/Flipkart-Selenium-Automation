package com.flipkart.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log4jHelper
 * <p>
 * Centralized access point for obtaining named Log4j2 loggers. Actual
 * configuration (console + rolling file appenders, log level, pattern)
 * lives in src/test/resources/log4j2.xml - this class just gives every other
 * class in the framework one consistent way to grab a logger instance
 * without repeating {@code LogManager.getLogger(...)} everywhere.
 */
public final class Log4jHelper {

    private Log4jHelper() {
        // utility class - no instances
    }

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }
}
