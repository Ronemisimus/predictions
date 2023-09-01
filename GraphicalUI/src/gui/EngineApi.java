package gui;

import dto.ReadFileDto;
import dto.ShowWorldDto;
import gui.details.tree.WorldDetailsItem;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import predictions.MainApi;
import predictions.MainApiImpl;

import java.io.File;

public class EngineApi {

    private final MainApi api;
    private static EngineApi instance;

    private EngineApi()
    {
        api = new MainApiImpl();
    }

    public static EngineApi getInstance()
    {
        if(instance == null)
        {
            instance = new EngineApi();
        }
        return instance;
    }

    public boolean LoadFile(StringProperty fileLabel)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Load XML File");
        try {
            String filePath = fileChooser.showOpenDialog(null).getAbsolutePath();
            ReadFileDto res = api.readFile(filePath);
            // TODO: check for errors and pop up error

            if (res.isFileLoaded())
            {
                fileLabel.setValue(filePath);
            }
            return res.isFileLoaded();
        }catch (NullPointerException e)
        {
            return false;
        }
    }

    public TreeItem<String> showLoadedWorld()
    {
        ShowWorldDto res = api.showLoadedWorld();
        return new WorldDetailsItem(res);
    }
}
