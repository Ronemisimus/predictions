package gui.history.display;

import dto.subdto.show.interactive.RunProgressDto;
import gui.EngineApi;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InteractiveRun {
    private final Integer identifier;
    private final LocalDateTime runTime;
    private final IntegerProperty simulationTick;
    private final Property<Integer> simulationTickMax;
    private final Property<Duration> simulationSecond, simulationSecondMax;

    private final Button stopButton, rerunButton, resumeButton, pauseButton;

    private final Property<ProgressBar> tickProgress, secondProgress;

    private final ScheduledExecutorService stateGetter;

    private Future<Void> task;
    public InteractiveRun(Integer identifier, LocalDateTime runTime) {
        this.identifier = identifier;
        this.runTime = runTime;
        this.simulationTick = new SimpleIntegerProperty();
        this.simulationTickMax = new SimpleObjectProperty<>();
        this.simulationSecond = new SimpleObjectProperty<>();
        this.simulationSecondMax = new SimpleObjectProperty<>();
        this.tickProgress = new SimpleObjectProperty<>();
        this.secondProgress = new SimpleObjectProperty<>();
        this.stopButton = new Button("Stop");
        this.rerunButton = new Button("Rerun");
        this.resumeButton = new Button("Resume");
        this.pauseButton = new Button("Pause");
        stopButton.setOnAction(e->{
            boolean stoppable = EngineApi.getInstance().stopSimulation(identifier);
            if (!stoppable) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "this simulation is not able to stop by the user");
                alert.show();
            }
        });
        pauseButton.setOnAction(e->EngineApi.getInstance().pauseSimulation(identifier));
        resumeButton.setOnAction(e->EngineApi.getInstance().resumeSimulation(identifier));
        rerunButton.setOnAction(e->EngineApi.getInstance().reRunSimulation(identifier));
        this.stateGetter = Executors.newScheduledThreadPool(1);
        task = null;
    }

    public SplitPane display() {
        //noinspection unchecked
        task = (Future<Void>) stateGetter.scheduleAtFixedRate(this::updateTask, 0, 500, TimeUnit.MILLISECONDS);
        SplitPane parent = new SplitPane();
        parent.setOrientation(Orientation.HORIZONTAL);
        VBox progress = new VBox();
        // Display Run Identifier
        addRunIdentifier(progress);

        // Display Run Time
        addRunTime(progress);

        // Display Simulation Tick
        addSimulationTick(progress);

        // Display Simulation Second
        addSimulationSecond(progress);

        Pane progressBarContainer = new Pane();
        linkProgressBars(progressBarContainer);
        progress.getChildren().add(progressBarContainer);

        // Simulation Controls (Buttons)
        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);
        pauseButton.prefWidthProperty().bind(controls.widthProperty().divide(2));
        resumeButton.prefWidthProperty().bind(controls.widthProperty().divide(2));
        stopButton.prefWidthProperty().bind(controls.widthProperty().divide(2));
        rerunButton.prefWidthProperty().bind(controls.widthProperty().divide(2));
        Platform.runLater(() -> controls.getChildren().addAll(pauseButton, resumeButton, stopButton, rerunButton));

        progress.setSpacing(5);
        progress.setPadding(new Insets(10,10,10,10));
        progress.setAlignment(Pos.TOP_CENTER);

        parent.getItems().addAll(progress,controls);

        // Add listeners or actions to the buttons as needed
        return parent;
    }

    public void hide(){
        task.cancel(true);
    }

    private void updateTask() {
        RunProgressDto res = EngineApi.getInstance().getRunProgress(identifier);
        Platform.runLater(() -> {
            this.simulationTick.setValue(res.getTick());
            this.simulationTickMax.setValue(res.getMaxTick());
            this.simulationSecond.setValue(res.getSecond());
            this.simulationSecondMax.setValue(res.getMaxSecond());
        });
    }

    private void linkProgressBars(Pane progressBarContainer) {
        // Add Progress Bar for Simulation Tick if available
        tickProgress.bind(Bindings.createObjectBinding(() -> {
            Integer maxTick = simulationTickMax.getValue(); // Retrieve the maximum duration (use your logic)
            if (maxTick != null) {
                double progress = (double) this.simulationTick.get() / simulationTickMax.getValue();
                return new ProgressBar(progress);
            }
            return null;
        }, this.simulationTick, this.simulationTickMax));


        // Add Progress Bar for Simulation Second if available
        secondProgress.bind(Bindings.createObjectBinding(() -> {
            Duration maxSecond = simulationSecondMax.getValue();
            Duration currentValue = this.simulationSecond.getValue();// Retrieve the maximum duration (use your logic)
            if (maxSecond != null) {
                double progress = (double) currentValue.toMillis() /maxSecond.toMillis();
                return new ProgressBar(progress);
            }
            return null;
        }, this.simulationSecond, this.simulationSecondMax));

        progressBarChangeHandler(progressBarContainer, tickProgress);
        progressBarChangeHandler(progressBarContainer, secondProgress);
    }

    private void progressBarChangeHandler(Pane progressBarContainer, Property<ProgressBar> progress) {
        progress.addListener((o, oldValue, newValue) -> {
            if (newValue!=null) {
                Platform.runLater(() ->{
                    progressBarContainer.getChildren().clear();
                    newValue.prefWidthProperty().bind(progressBarContainer.widthProperty());
                    progressBarContainer.getChildren().add(newValue);
                });
            }
            else {
                Platform.runLater(() -> progressBarContainer.getChildren().clear());
            }
        });
    }

    private void addSimulationSecond(VBox parent) {
        Label simulationSecond = new Label();
        // if simulation second is null show N/A else show simulation second
        simulationSecond.textProperty().bind(Bindings.createStringBinding(() ->
                this.simulationSecond.getValue()==null? "N/A" :
                        this.simulationSecondMax.getValue()==null?
                                prettyFormatDuration(this.simulationSecond.getValue()):
                prettyFormatDuration(this.simulationSecond.getValue()) +"/" + prettyFormatDuration(this.simulationSecondMax.getValue()),
                this.simulationSecond, this.simulationSecondMax)
        );
        parent.getChildren().addAll(new Label("Simulation Second:"), simulationSecond);
    }

    private void addSimulationTick(VBox parent) {
        Label simulationTick = new Label();
        // if simulation tick is null show N/A else show simulation tick
        simulationTick.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.simulationTick.getValue() == null)
            {
                return "N/A";
            }
            else{
                if (this.simulationTickMax.getValue()==null) {
                    return this.simulationTick.getValue().toString();
                }
                else {
                    return this.simulationTick.getValue().toString() +"/" +
                            this.simulationTickMax.getValue().toString();
                }
            }
        }, this.simulationTick, this.simulationTickMax));
        parent.getChildren().addAll(new Label("Simulation Tick:"), simulationTick);
    }

    private void addRunTime(VBox parent) {
        Label runTimeLabel = new Label("Run Time:");
        LocalDateTime runTimeValue = runTime; // Retrieve the run time (use your logic)
        Label runTime = new Label(runTimeValue != null ? runTimeValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A");
        parent.getChildren().addAll(runTimeLabel, runTime);
    }

    private void addRunIdentifier(VBox parent) {
        Label runIdentifierLabel = new Label("Run Identifier: " + Long.valueOf(identifier));
        parent.getChildren().addAll(runIdentifierLabel);
    }

    private String prettyFormatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
