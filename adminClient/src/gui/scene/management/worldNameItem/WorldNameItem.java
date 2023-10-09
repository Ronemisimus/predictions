package gui.scene.management.worldNameItem;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class WorldNameItem {
    private final String hyperlink;
    private final String name;
    public WorldNameItem(String name, String hyperlink) {
        this.hyperlink = hyperlink;
        this.name = name;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public String getName() {
        return name;
    }

    private void handleHyperlinkClick(ActionEvent actionEvent) {
        String filePath = getHyperlink();
        new Thread(() -> {
            // Check if Desktop is supported (i.e., the application is running in a GUI environment)
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                File fileToOpen = new File(filePath);

                try {
                    // Check if the file/folder exists before attempting to open it
                    if (fileToOpen.exists()) {
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            desktop.open(fileToOpen);
                        } else {
                            System.err.println("Desktop is not supported.");
                        }
                    } else {
                        System.out.println("File or folder does not exist: " + filePath);
                    }
                } catch (IOException e) {
                    System.err.println("Error opening file/folder: " + filePath);
                    e.printStackTrace(System.err);
                }
            } else {
                System.err.println("Desktop is not supported.");
            }
        }).start();
    }

    @Override
    public String toString() {
        return name;
    }
}
