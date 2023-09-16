package gui.history.display;

import dto.subdto.show.instance.RunStateDto;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.Map;

public class RunDisplayed extends HBox {

    private final Map.Entry<Integer, LocalDateTime> entry;

    private final RunStateDto runStateDto;
    public RunDisplayed(Map.Entry<Integer, LocalDateTime> entry, RunStateDto runStateDto) {
        super();
        this.entry = entry;
        this.runStateDto = runStateDto;
        setBackground(new Background(new BackgroundFill(Paint.valueOf(getColor()), null, null)));
        Text text = new Text(entry.getKey() + " " + entry.getValue());
        this.getChildren().add(text);

    }

    private String getColor() {
        if (runStateDto.getRunning())
        {
            return "green";
        }
        if (runStateDto.getFinished())
        {
            return "blue";
        }
        if (runStateDto.getStopped())
        {
            return "red";
        }
        if(runStateDto.getPaused())
        {
            return "yellow";
        }
        throw new RuntimeException("Unknown state");
    }

    public Integer getRunIdentifier() {
        return entry.getKey();
    }

    public LocalDateTime getRunTime() {
        return entry.getValue();
    }
}
