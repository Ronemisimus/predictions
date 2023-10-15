package gui.scene.allocations;

import gui.scene.SceneController;
import gui.util.ServerApi;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Allocations implements SceneController {
    @FXML
    private ScrollPane mainRoot;
    @FXML
    public TableView<RequestDetailsRow> tableView;

    private ScheduledExecutorService requestsGetter;

    private static Allocations allocationsController = null;

    public static SceneController getInstance() {
        return allocationsController;
    }

    @FXML
    public void initialize() {
        allocationsController = this;
        requestsGetter = Executors.newScheduledThreadPool(1);
        requestsGetter.scheduleAtFixedRate(this::updateTable, 0, 1000, TimeUnit.MILLISECONDS);
        tableView.prefWidthProperty().bind(Bindings.max(1800, mainRoot.widthProperty().subtract(3)));
        tableView.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(3)));
        initializeTable();
    }

    private void initializeTable() {
        TableColumn<RequestDetailsRow, Integer> idColumn = getRequestDetailsRowIntegerTableColumn(
                "Id", cellData -> cellData.getValue().requestIdProperty().asObject());
        TableColumn<RequestDetailsRow, String> userColumn = getRequestDetailsRowStringTableColumn(
                "User", cellData -> cellData.getValue().requestingUserProperty());
        TableColumn<RequestDetailsRow, String> worldName = getRequestDetailsRowStringTableColumn(
                "World",cellData -> cellData.getValue().worldNameProperty());
        TableColumn<RequestDetailsRow, Integer> runAmount = getRequestDetailsRowIntegerTableColumn(
                "Runs Requested",cellData -> cellData.getValue().runAmountProperty().asObject());
        TableColumn<RequestDetailsRow, String> ticksTermination = getRequestDetailsRowStringTableColumn(
                "Ticks Termination",cellData -> cellData.getValue().ticksTerminationProperty());
        TableColumn<RequestDetailsRow, String> secondsTermination = getRequestDetailsRowStringTableColumn(
                "Seconds Termination",cellData -> cellData.getValue().secondsTerminationProperty());
        TableColumn<RequestDetailsRow, Boolean> userTermination = getRequestDetailsRowBooleanTableColumn(
                cellData -> cellData.getValue().userTerminationProperty().asObject());
        TableColumn<RequestDetailsRow, String> status = getRequestDetailsRowStringTableColumn(
                "Request Status",cellData -> cellData.getValue().statusProperty());
        TableColumn<RequestDetailsRow, Integer> runsUsed = getRequestDetailsRowIntegerTableColumn(
                "Runs Used",cellData -> cellData.getValue().runsUsedProperty().asObject());
        TableColumn<RequestDetailsRow, Integer> runsCompleted = getRequestDetailsRowIntegerTableColumn(
                "Runs Completed",cellData -> cellData.getValue().runsCompletedProperty().asObject());
        TableColumn<RequestDetailsRow, Integer> runsCurrentlyRunning = getRequestDetailsRowIntegerTableColumn(
                "Runs Currently Running",cellData -> cellData.getValue().runsCurrentlyRunningProperty().asObject());
        TableColumn<RequestDetailsRow, Button> approveButton = new TableColumn<>("Approve");
        approveButton.setCellValueFactory(cellData -> cellData.getValue().approveButtonProperty());
        TableColumn<RequestDetailsRow, Button> rejectButton = new TableColumn<>("Reject");
        rejectButton.setCellValueFactory(cellData -> cellData.getValue().rejectButtonProperty());

        List<TableColumn<RequestDetailsRow,?>> columns = Arrays.asList(idColumn,
                userColumn,
                worldName,
                runAmount,
                ticksTermination,
                secondsTermination,
                userTermination,
                status,
                runsUsed,
                runsCompleted,
                runsCurrentlyRunning,
                approveButton,
                rejectButton);

        columns.forEach(column -> column.prefWidthProperty().setValue(getTitleWidth(column)));

        tableView.getColumns().addAll(columns);
    }

    @NotNull
    private static TableColumn<RequestDetailsRow, String> getRequestDetailsRowStringTableColumn(
            String title,
            Callback<TableColumn.CellDataFeatures<RequestDetailsRow,String>, ObservableValue<String>> cell
    ) {
        TableColumn<RequestDetailsRow, String> userColumn = new TableColumn<>(title);
        userColumn.setCellValueFactory(cell);
        userColumn.setCellFactory((column)-> new TableCell<RequestDetailsRow, String>() {
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

    @NotNull
    private static TableColumn<RequestDetailsRow, Boolean> getRequestDetailsRowBooleanTableColumn(
            Callback<TableColumn.CellDataFeatures<RequestDetailsRow,Boolean>, ObservableValue<Boolean>> cell
    ) {
        TableColumn<RequestDetailsRow, Boolean> userColumn = new TableColumn<>("User Termination");
        userColumn.setCellValueFactory(cell);
        userColumn.setCellFactory((column)-> new TableCell<RequestDetailsRow, Boolean>() {
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
    private static TableColumn<RequestDetailsRow, Integer> getRequestDetailsRowIntegerTableColumn(
            String title,
            Callback<TableColumn.CellDataFeatures<RequestDetailsRow,Integer>, ObservableValue<Integer>> cell) {
        TableColumn<RequestDetailsRow, Integer> idColumn = new TableColumn<>(title);
        idColumn.setCellValueFactory(cell);
        idColumn.setCellFactory(cellData -> new TableCell<RequestDetailsRow, Integer>() {
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

    private static void updateColumnWidth(TableColumn<RequestDetailsRow, ?> tableColumn, String item) {
        Text text = new Text(item);
        double width = text.getLayoutBounds().getWidth() + 50;
        double width2 = tableColumn.getWidth();
        width = Math.max(width, width2);
        tableColumn.setPrefWidth(width);
    }

    private Double getTitleWidth(TableColumn<RequestDetailsRow,?> column) {
        String title = column.getText();
        Text text = new Text(title);
        text.applyCss();
        return text.getLayoutBounds().getWidth() + 50;
    }

    private void updateTable() {
        List<RequestDetailsRow> requests = ServerApi.getInstance().getRequests();
        List<RequestDetailsRow> added = requests.stream()
                .filter(item->!tableView.getItems().contains(item))
                .collect(Collectors.toList());
        List<RequestDetailsRow> removed = tableView.getItems().stream()
                .filter(item->!requests.contains(item))
                .collect(Collectors.toList());
        tableView.getItems().addAll(added);
        tableView.getItems().removeAll(removed);
    }

    @Override
    public void destroy() {
        requestsGetter.shutdownNow();
    }
}
