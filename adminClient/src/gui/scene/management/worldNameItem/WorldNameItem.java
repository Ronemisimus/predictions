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

    @Override
    public String toString() {
        return name;
    }
}
