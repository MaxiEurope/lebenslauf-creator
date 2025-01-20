package org.lebenslauf.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility class for displaying different types of dialogs.
 */
public class DialogUtils {

    /**
     * Show an error dialog.
     *
     * @param message the message to display
     * @param title the title of the dialog
     */
    public static void showErrorDialog(String message, String title) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show an information dialog.
     *
     * @param message the message to display
     * @param title the title of the dialog
     */
    public static void showInfoDialog(String message, String title) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
