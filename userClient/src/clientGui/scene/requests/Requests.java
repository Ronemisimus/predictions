package clientGui.scene.requests;

import clientGui.scene.SceneController;
import clientGui.util.ServerApi;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Requests implements SceneController {
    @FXML
    private TableView<RequestsDetailsRow> tableView;
    @FXML
    private ScrollPane tableLimit;
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

        tableView.prefWidthProperty().bind(Bindings.max(1600, tableLimit.widthProperty().subtract(3)));
        tableView.prefHeightProperty().bind(Bindings.max(600, tableLimit.heightProperty().subtract(3)));
    }

    private void initializeTableView() {
        TableColumn<RequestsDetailsRow, Integer> idColumn = getRequestDetailsRowIntegerTableColumn(
                "Id",cellData -> cellData.getValue().requestIdProperty().asObject());
        TableColumn<RequestsDetailsRow, String> worldName = getRequestDetailsRowStringTableColumn(
                "World",cellData -> cellData.getValue().worldNameProperty());
        TableColumn<RequestsDetailsRow, Integer> runAmount = getRequestDetailsRowIntegerTableColumn(
                "Runs Requested",cellData -> cellData.getValue().runAmountProperty().asObject());
        TableColumn<RequestsDetailsRow, String> ticksTermination = getRequestDetailsRowStringTableColumn(
                "Ticks Termination",cellData -> cellData.getValue().ticksTerminationProperty());
        TableColumn<RequestsDetailsRow, String> secondsTermination = getRequestDetailsRowStringTableColumn(
                "Seconds Termination",cellData -> cellData.getValue().secondsTerminationProperty());
        TableColumn<RequestsDetailsRow, Boolean> userTermination = getRequestDetailsRowBooleanTableColumn(
                cellData -> cellData.getValue().userTerminationProperty().asObject());
        TableColumn<RequestsDetailsRow, String> status = getRequestDetailsRowStringTableColumn(
                "Request Status",cellData -> cellData.getValue().statusProperty());
        TableColumn<RequestsDetailsRow, Integer> runsUsed = getRequestDetailsRowIntegerTableColumn(
                "Runs Used",cellData -> cellData.getValue().runsUsedProperty().asObject());
        TableColumn<RequestsDetailsRow, Integer> runsCompleted = getRequestDetailsRowIntegerTableColumn(
                "Runs Completed",cellData -> cellData.getValue().runsCompletedProperty().asObject());
        TableColumn<RequestsDetailsRow, Integer> runsCurrentlyRunning = getRequestDetailsRowIntegerTableColumn(
                "Runs Currently Running",cellData -> cellData.getValue().runsCurrentlyRunningProperty().asObject());
        //noinspection unchecked
        tableView.getColumns().addAll(idColumn, worldName, runAmount, ticksTermination, secondsTermination, userTermination, status, runsUsed, runsCompleted, runsCurrentlyRunning);

        tableView.getColumns().forEach(col->updateColumnWidth(col, col.getText()));

    }

    @NotNull
    private static TableColumn<RequestsDetailsRow, String> getRequestDetailsRowStringTableColumn(
            String title,
            Callback<TableColumn.CellDataFeatures<RequestsDetailsRow,String>, ObservableValue<String>> cell
    ) {
        TableColumn<RequestsDetailsRow, String> userColumn = new TableColumn<>(title);
        userColumn.setCellValueFactory(cell);
        userColumn.setCellFactory((column)-> new TableCell<RequestsDetailsRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    updateColumnWidth(getTableColumn(), item);
                }
            }
        });
        return userColumn;
    }

    private static void updateColumnWidth(TableColumn<RequestsDetailsRow, ?> tableColumn, String item) {
        Text text = new Text(item);
        double width = text.getLayoutBounds().getWidth() + 50;
        double width2 = tableColumn.getWidth();
        width = Math.max(width, width2);
        tableColumn.setPrefWidth(width);
    }

    @NotNull
    private static TableColumn<RequestsDetailsRow, Boolean> getRequestDetailsRowBooleanTableColumn(
            Callback<TableColumn.CellDataFeatures<RequestsDetailsRow,Boolean>, ObservableValue<Boolean>> cell
    ) {
        TableColumn<RequestsDetailsRow, Boolean> userColumn = new TableColumn<>("User Termination");
        userColumn.setCellValueFactory(cell);
        userColumn.setCellFactory((column)-> new TableCell<RequestsDetailsRow, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                    updateColumnWidth(getTableColumn(), item.toString());
                }
            }
        });
        return userColumn;
    }

    @NotNull
    private static TableColumn<RequestsDetailsRow, Integer> getRequestDetailsRowIntegerTableColumn(
            String title,
            Callback<TableColumn.CellDataFeatures<RequestsDetailsRow,Integer>, ObservableValue<Integer>> cell) {
        TableColumn<RequestsDetailsRow, Integer> idColumn = new TableColumn<>(title);
        idColumn.setCellValueFactory(cell);
        idColumn.setCellFactory(cellData -> new TableCell<RequestsDetailsRow, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(item));
                    updateColumnWidth(getTableColumn(), String.valueOf(item));
                }
            }
        });
        return idColumn;
    }

    private void updateRequestTable() {
        List<RequestsDetailsRow> requests = ServerApi.getInstance().getRequestsRows();
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
