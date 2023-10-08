package gui.scene.management.worldNameItem;

import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WorldNameItem extends HBox {
    private final Hyperlink hyperlink;
    private final Text name;

    public WorldNameItem(String name, String hyperlink) {
        this.hyperlink = new Hyperlink(hyperlink);
        this.name = new Text(name);
        this.getChildren().add(this.name);
        this.getChildren().add(this.hyperlink);
        this.hyperlink.setOnAction(this::handleHyperlinkClick);
    }

    public Hyperlink getHyperlink() {
        return hyperlink;
    }

    public Text getName() {
        return name;
    }

    private void handleHyperlinkClick(ActionEvent actionEvent) {
        String filePath = getHyperlink().getText();
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

}
