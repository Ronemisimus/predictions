package clientGui.scene.newExecution;

import clientGui.execution.environment.EntityAmountGetter;
import clientGui.execution.environment.EnvironmentVariableGetter;
import clientGui.scene.SceneController;
import clientGui.scene.main.MainScene;
import clientGui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NewExecutionScene implements SceneController {

    @FXML
    private Button clearButton;
    @FXML
    private Button startButton;
    @FXML
    private BorderPane leftPane;
    @FXML
    private BorderPane centerPane;
    @FXML
    private ComboBox<RequestSelection> requestSelected;

    private static NewExecutionScene instance;

    private ScheduledExecutorService requestGetter;

    public static SceneController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        instance = this;

        clearButton.setOnAction(this::handleClearButtonClick);
        startButton.setOnAction(this::handleStartButtonClick);

        requestSelected.valueProperty().addListener(this::selectionChanged);

        requestGetter = Executors.newScheduledThreadPool(1);
        requestGetter.scheduleAtFixedRate(this::updateRequestSelected, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void selectionChanged(Observable observable, RequestSelection oldValue, RequestSelection newValue) {
        System.out.println("selection changed");
        new Thread(() -> {

            if (oldValue != null) {
                ServerApi.getInstance().clearSimulation(oldValue.getRequestId());
            }
            if (newValue != null) {
                ServerApi.getInstance().setSimulation(newValue.getRequestId());
                List<EntityAmountGetter> roots = ServerApi.getInstance().getEntityAmounts();
                List<EnvironmentVariableGetter> environmentVariables = ServerApi.getInstance().getEnvironmentVariables();
                Platform.runLater(() -> {
                    VBox res = new VBox();
                    res.getChildren().addAll(roots);
                    leftPane.setCenter(res);
                    VBox env = new VBox();
                    env.getChildren().addAll(environmentVariables);
                    centerPane.setCenter(env);
                    requestSelected.setValue(newValue);
                });
            }
        }).start();
    }

    private void updateRequestSelected() {
        System.out.println("update request selected");
        List<RequestSelection> requests = ServerApi.getInstance().getApprovedOpenRequests();
        List<RequestSelection> added = requests.stream()
                .filter(name -> !requestSelected.getItems().contains(name))
                .collect(Collectors.toList());
        List<RequestSelection> removed = requestSelected.getItems().stream()
                .filter(name -> !requests.contains(name))
                .collect(Collectors.toList());
        Platform.runLater(()-> {
            requestSelected.getItems().addAll(added);
            requestSelected.getItems().removeAll(removed);
        });
    }

    private void handleClearButtonClick(ActionEvent event) {
        if (requestSelected.getValue() != null) ServerApi.getInstance().clearSimulation(requestSelected.getValue().getRequestId());
        //noinspection DataFlowIssue
        MainScene.getInstance(null).fireNewExecutionButton();
    }

    private void handleStartButtonClick(ActionEvent event) {
        ServerApi.getInstance().runSimulation();
        //noinspection DataFlowIssue
        MainScene.getInstance(null).fireResultsButton();
    }

    @Override
    public void destroy() {
        requestGetter.shutdownNow();
    }
}