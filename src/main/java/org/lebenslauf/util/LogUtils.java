package org.lebenslauf.util;

import java.io.IOException;
import java.util.logging.*;

/**
 * Utility class for logging messages with different levels.
 * Logs go to "lebenslauf.log" and the console.
 */
public class LogUtils {
    private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("lebenslauf.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomColorFormatter());
            consoleHandler.setLevel(Level.ALL);

            LOGGER.setUseParentHandlers(false);

            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);

            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize FileHandler for logging: " + e.getMessage());
        }
    }

    /**
     * Log an info message.
     *
     * @param message the message to log
     */
    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Log an error message.
     *
     * @param ex the exception to log
     * @param message the message to log
     */
    public static void logError(Throwable ex, String message) {
        LOGGER.log(Level.SEVERE, message, ex);
    }

    /**
     * Log a warning message.
     *
     * @param message the message to log
     */
    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }
}

/**
 * Custom formatter for coloring log messages.
 * 
 * INFO: yellow
 * WARNING: white
 * SEVERE: red
 */
class CustomColorFormatter extends Formatter {
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE = "\u001B[37m";
    private static final String RED = "\u001B[31m";

    @Override
    public String format(LogRecord record) {
        String color = switch (record.getLevel().getName()) {
            case "INFO" -> YELLOW;
            case "WARNING" -> WHITE;
            case "SEVERE" -> RED;
            default -> RESET;
        };
        return color + record.getLevel() + ": " + record.getMessage() + RESET + "\n";
    }
}
