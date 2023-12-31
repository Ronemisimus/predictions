package gui.history.display;

import dto.subdto.show.instance.RunStateDto;
import gui.history.data.RunState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RunDisplayed extends HBox {

    private final Map.Entry<Integer, LocalDateTime> entry;

    private final ObservableList<Parent> entityChartLabels = FXCollections.observableArrayList();

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RunStateDto runStateDto;

    private final InteractiveRun interactiveRun;

    private final EntityCountDisplay entityCount;
    public RunDisplayed(Map.Entry<Integer, LocalDateTime> entry, RunStateDto runStateDto) {
        super();
        this.entry = entry;
        this.runStateDto = runStateDto;
        setBackground(new Background(new BackgroundFill(getColor(), null, null)));
        Text text = new Text(formatter.format(entry.getValue()));
        this.getChildren().add(text);
        this.interactiveRun = new InteractiveRun(entry.getKey(), entry.getValue());
        this.entityCount = new EntityCountDisplay(entry.getKey());
    }

    private Color getColor() {
        if (runStateDto.getRunning())
        {
            return Color.color(0.0, 1.0, 0.0, 0.2);
        }
        if (runStateDto.getFinished())
        {
            return Color.color(0.0, 0.0, 1.0, 0.2);
        }
        if (runStateDto.getStopped())
        {
            return Color.color(1.0, 0.0, 0.0, 0.2);
        }
        if(runStateDto.getPaused())
        {
            return Color.color(1.0, 1.0, 0.0, 0.2);
        }
        else
        {
            return Color.color(1.0, 0.0, 1.0, 0.2);
        }
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
        else return RunState.WAITING;
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

    public ObservableList<Parent> getEntityChartLabels() {
        return entityChartLabels;
    }

    public void removeEntityChartLabels(List<Parent> entityChartLabel) {
        Platform.runLater(() -> entityChartLabels.removeAll(entityChartLabel));
    }

    public void addEntityChartLabels(List<Parent> entityChartLabel) {
        Platform.runLater(() -> entityChartLabels.addAll(entityChartLabel));
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, runStateDto);
    }

    public InteractiveRun getInteractiveRun() {
        return interactiveRun;
    }

    public EntityCountDisplay getEntityCount() {
        return entityCount;
    }
}
