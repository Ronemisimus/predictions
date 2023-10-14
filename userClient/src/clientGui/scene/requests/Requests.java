package clientGui.scene.requests;

import clientGui.scene.SceneController;
import clientGui.util.ServerApi;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Requests implements SceneController {
    @FXML
    public TableView<RequestsDetailsRow> tableView;
    @FXML
    private ComboBox<String> worldNameComboBox;
    @FXML
    private TextField runAmountTextField;
    @FXML
    private CheckBox userTerminationCheckBox;
    @FXML
    private CheckBox ticksTerminationCheckBox;
    @FXML
    private TextField ticksTerminationTextField;
    @FXML
    private CheckBox secondsTerminationCheckBox;
    @FXML
    private TextField secondsTerminationTextField;
    @FXML
    private Button submitButton;

    private ScheduledExecutorService worldGetter, requestGetter;
    private static Requests requestsController = null;

    public static Requests getInstance() {
        return requestsController;
    }

    @FXML
    private void initialize() {
        worldGetter = Executors.newScheduledThreadPool(1);
        worldGetter.scheduleAtFixedRate(this::updateWorldNameComboBox, 0, 500, TimeUnit.MILLISECONDS);

        requestGetter = Executors.newScheduledThreadPool(1);
        requestGetter.scheduleAtFixedRate(this::updateRequestTable, 0, 500, TimeUnit.MILLISECONDS);

        ticksTerminationTextField.disableProperty().bind(ticksTerminationCheckBox.selectedProperty().not());
        secondsTerminationTextField.disableProperty().bind(secondsTerminationCheckBox.selectedProperty().not());
        ticksTerminationCheckBox.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue) ticksTerminationTextField.setText("");
        });
        secondsTerminationCheckBox.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue) secondsTerminationTextField.setText("");
        });

        runAmountTextField.setTextFormatter(new TextFormatter<>(this::change));
        secondsTerminationTextField.setTextFormatter(new TextFormatter<>(this::change));
        ticksTerminationTextField.setTextFormatter(new TextFormatter<>(this::change));

        initializeTableView();


        submitButton.setOnAction(this::submitRequest);
        requestsController = this;
    }

    private void initializeTableView() {
        TableColumn<RequestsDetailsRow, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().requestIdProperty().asObject());
        TableColumn<RequestsDetailsRow, String> worldName = new TableColumn<>("World");
        worldName.setCellValueFactory(cellData -> cellData.getValue().worldNameProperty());
        TableColumn<RequestsDetailsRow, Integer> runAmount = new TableColumn<>("Runs Requested");
        runAmount.setCellValueFactory(cellData -> cellData.getValue().runAmountProperty().asObject());
        TableColumn<RequestsDetailsRow, String> ticksTermination = new TableColumn<>("Ticks Termination");
        ticksTermination.setCellValueFactory(cellData -> cellData.getValue().ticksTerminationProperty());
        TableColumn<RequestsDetailsRow, String> secondsTermination = new TableColumn<>("Seconds Termination");
        secondsTermination.setCellValueFactory(cellData -> cellData.getValue().secondsTerminationProperty());
        TableColumn<RequestsDetailsRow, Boolean> userTermination = new TableColumn<>("User Termination");
        userTermination.setCellValueFactory(cellData -> cellData.getValue().userTerminationProperty().asObject());
        TableColumn<RequestsDetailsRow, String> status = new TableColumn<>("Request Status");
        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        TableColumn<RequestsDetailsRow, Integer> runsUsed = new TableColumn<>("Runs Used");
        runsUsed.setCellValueFactory(cellData -> cellData.getValue().runsUsedProperty().asObject());
        TableColumn<RequestsDetailsRow, Integer> runsCompleted = new TableColumn<>("Runs Completed");
        runsCompleted.setCellValueFactory(cellData -> cellData.getValue().runsCompletedProperty().asObject());
        TableColumn<RequestsDetailsRow, Integer> runsCurrentlyRunning = new TableColumn<>("Runs Currently Running");
        runsCurrentlyRunning.setCellValueFactory(cellData -> cellData.getValue().runsCurrentlyRunningProperty().asObject());
        //noinspection unchecked
        tableView.getColumns().addAll(idColumn, worldName, runAmount, ticksTermination, secondsTermination, userTermination, status, runsUsed, runsCompleted, runsCurrentlyRunning);
    }

    private void updateRequestTable() {
        List<RequestsDetailsRow> requests = ServerApi.getInstance().getRequests();
        List<RequestsDetailsRow> added = requests.stream()
                .filter(request -> !tableView.getItems().contains(request)).collect(Collectors.toList());
        List<RequestsDetailsRow> removed = tableView.getItems().stream()
                .filter(request -> !requests.contains(request)).collect(Collectors.toList());
        Platform.runLater(() -> {
            tableView.getItems().addAll(added);
            tableView.getItems().removeAll(removed);
        });
    }

    private void updateWorldNameComboBox() {
        List<String> worlds = ServerApi.getInstance().getLoadedWorlds();
        List<String> added = worlds.stream().filter(name -> !worldNameComboBox.getItems().contains(name)).collect(Collectors.toList());
        List<String> removed = worldNameComboBox.getItems().stream().filter(name -> !worlds.contains(name)).collect(Collectors.toList());
        Platform.runLater(() -> {
            worldNameComboBox.getItems().addAll(added);
            worldNameComboBox.getItems().removeAll(removed);
        });
    }

    private void submitRequest(ActionEvent actionEvent) {
        String worldName = worldNameComboBox.getValue();
        Integer runAmount = runAmountTextField.getText().isEmpty()?null:Integer.parseInt(runAmountTextField.getText());
        Boolean userTermination = userTerminationCheckBox.isSelected();
        Integer ticksTermination = ticksTerminationCheckBox.isSelected()?
                (ticksTerminationTextField.getText().isEmpty()?null:
                        Integer.parseInt(ticksTerminationTextField.getText())):null;
        Integer secondsTermination = secondsTerminationCheckBox.isSelected()?
                (secondsTerminationTextField.getText().isEmpty()?null:
                        Integer.parseInt(secondsTerminationTextField.getText())):null;
        ServerApi.getInstance().submitRequest(worldName, runAmount, userTermination, ticksTermination, secondsTermination);
    }

    private TextFormatter.Change change(TextFormatter.Change change) {
        if (change.getControlNewText().matches("\\d*")) {
            return change;
        }
        return null;
    }

    @Override
    public void destroy() {
        this.worldGetter.shutdownNow();
        this.requestGetter.shutdownNow();
    }
}
