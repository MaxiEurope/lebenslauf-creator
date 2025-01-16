package org.lebenslauf.util;

import java.io.IOException;
import java.util.logging.*;

public class LogUtils {
    private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("lebenslauf.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            LOGGER.setUseParentHandlers(false);

            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(new ConsoleHandler());

            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize FileHandler for logging: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void logError(Throwable ex, String message) {
        LOGGER.log(Level.SEVERE, message, ex);
    }

    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }
}
