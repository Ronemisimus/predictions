package gui.execution.scene;

import gui.EngineApi;
import gui.execution.environment.EntityAmountGetter;
import gui.execution.environment.EnvironmentVariableGetter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ExecutionController {

    @FXML
    private Button clearButton;
    @FXML
    private Button startButton;
    @FXML
    private BorderPane leftPane;
    @FXML
    private BorderPane centerPane;

    @FXML
    public void initialize() {
        List<EntityAmountGetter> roots = EngineApi.getInstance().getEntityAmounts();
        VBox res = new VBox();
        res.getChildren().addAll(roots);
        leftPane.setCenter(res);

        List<EnvironmentVariableGetter> environmentVariables = EngineApi.getInstance().getEnvironmentVariables();
        VBox env = new VBox();
        env.getChildren().addAll(environmentVariables);
        centerPane.setCenter(env);
    }
}
