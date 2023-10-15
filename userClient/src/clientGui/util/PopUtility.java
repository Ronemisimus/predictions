package clientGui.util;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class PopUtility {
    public static void openPopup(Window ownerWindow, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.initOwner(ownerWindow);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Steal focus from the owner window
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        // Show the popup and wait for user interaction
        alert.showAndWait();

        // Return the focus to the owner window when the popup is closed
        ownerWindow.requestFocus();
    }
}
