package gui.scene.allocations;

import gui.scene.SceneController;
import gui.util.ServerApi;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
        tableView.prefWidthProperty().bind(Bindings.max(900, mainRoot.widthProperty().subtract(3)));
        tableView.prefHeightProperty().bind(Bindings.max(600, mainRoot.heightProperty().subtract(3)));
        initializeTable();
    }

    private void initializeTable() {
        TableColumn<RequestDetailsRow, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().requestIdProperty().asObject());
        TableColumn<RequestDetailsRow, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(cellData -> cellData.getValue().requestingUserProperty());
        TableColumn<RequestDetailsRow, String> worldName = new TableColumn<>("World");
        worldName.setCellValueFactory(cellData -> cellData.getValue().worldNameProperty());
        TableColumn<RequestDetailsRow, Integer> runAmount = new TableColumn<>("Runs Requested");
        runAmount.setCellValueFactory(cellData -> cellData.getValue().runAmountProperty().asObject());
        TableColumn<RequestDetailsRow, String> ticksTermination = new TableColumn<>("Ticks Termination");
        ticksTermination.setCellValueFactory(cellData -> cellData.getValue().ticksTerminationProperty());
        TableColumn<RequestDetailsRow, String> secondsTermination = new TableColumn<>("Seconds Termination");
        secondsTermination.setCellValueFactory(cellData -> cellData.getValue().secondsTerminationProperty());
        TableColumn<RequestDetailsRow, Boolean> userTermination = new TableColumn<>("User Termination");
        userTermination.setCellValueFactory(cellData -> cellData.getValue().userTerminationProperty().asObject());
        TableColumn<RequestDetailsRow, String> status = new TableColumn<>("Request Status");
        status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        TableColumn<RequestDetailsRow, Integer> runsUsed = new TableColumn<>("Runs Used");
        runsUsed.setCellValueFactory(cellData -> cellData.getValue().runsUsedProperty().asObject());
        TableColumn<RequestDetailsRow, Integer> runsCompleted = new TableColumn<>("Runs Completed");
        runsCompleted.setCellValueFactory(cellData -> cellData.getValue().runsCompletedProperty().asObject());
        TableColumn<RequestDetailsRow, Integer> runsCurrentlyRunning = new TableColumn<>("Runs Currently Running");
        runsCurrentlyRunning.setCellValueFactory(cellData -> cellData.getValue().runsCurrentlyRunningProperty().asObject());
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

        columns.forEach(column -> column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size())));


        tableView.getColumns().addAll(columns);
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
