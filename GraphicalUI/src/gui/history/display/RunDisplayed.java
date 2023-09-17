package gui.history.display;

import dto.subdto.show.instance.RunStateDto;
import gui.history.data.RunState;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

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

    public RunState getRunState() {
        if (runStateDto.getPaused()) return RunState.PAUSED;
        if (runStateDto.getStopped()) return RunState.STOPPED;
        if (runStateDto.getFinished()) return RunState.FINISHED;
        if (runStateDto.getRunning()) return RunState.RUNNING;
        throw new RuntimeException("Unknown state");
    }

    @Override
    public String toString() {
        return "RunDisplayed{" +
                "entry=" + entry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunDisplayed that = (RunDisplayed) o;
        return Objects.equals(entry, that.entry) &&
                Objects.equals(runStateDto.getRunning(), that.runStateDto.getRunning()) &&
                Objects.equals(runStateDto.getFinished(), that.runStateDto.getFinished()) &&
                Objects.equals(runStateDto.getStopped(), that.runStateDto.getStopped()) &&
                Objects.equals(runStateDto.getPaused(), that.runStateDto.getPaused());
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, runStateDto);
    }
}
