package gui.scene.allocations;

import dto.subdto.requests.RequestDetailsDto;
import gui.util.ServerApi;
import gui.util.Username;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class RequestDetailsRow {
    private final IntegerProperty requestId;
    private final StringProperty requestingUser;
    private final StringProperty worldName;
    private final IntegerProperty runAmount;
    private final StringProperty ticksTermination;
    private final StringProperty secondsTermination;
    private final BooleanProperty userTermination;
    private final StringProperty status;
    private final IntegerProperty runsUsed;
    private final IntegerProperty runsCompleted;
    private final IntegerProperty runsCurrentlyRunning;

    private final ObservableValue<Button> approveButton, rejectButton;

    public RequestDetailsRow(RequestDetailsDto requestDetailsDto) {
        requestId = new SimpleIntegerProperty(requestDetailsDto.getRequestId());
        requestingUser = new SimpleStringProperty(Username.unwrap(requestDetailsDto.getUsername()));
        worldName = new SimpleStringProperty(requestDetailsDto.getWorldName());
        runAmount = new SimpleIntegerProperty(requestDetailsDto.getRunAllocation());
        ticksTermination = new SimpleStringProperty(requestDetailsDto.getTickLimit()==null?"-":requestDetailsDto.getTickLimit().toString());
        secondsTermination = new SimpleStringProperty(requestDetailsDto.getSecondsLimit()==null?"-":requestDetailsDto.getSecondsLimit().toString());
        userTermination = new SimpleBooleanProperty(requestDetailsDto.isUserTermination());
        status = new SimpleStringProperty(requestDetailsDto.getStatus());
        runsUsed = new SimpleIntegerProperty(requestDetailsDto.getRunsUsed());
        runsCompleted = new SimpleIntegerProperty(requestDetailsDto.getRunsCompleted());
        runsCurrentlyRunning = new SimpleIntegerProperty(requestDetailsDto.getRunsCurrentlyRunning());
        Button approveButton = new Button("Approve"), rejectButton = new Button("Reject");
        approveButton.setDisable(!requestDetailsDto.getStatus().equals("WAITING"));
        rejectButton.setDisable(!requestDetailsDto.getStatus().equals("WAITING"));
        this.approveButton = new SimpleObjectProperty<>(approveButton);
        this.approveButton.getValue().setOnAction(this::approveButton);
        this.rejectButton = new SimpleObjectProperty<>(rejectButton);
        this.rejectButton.getValue().setOnAction(this::rejectButton);
    }

    private void rejectButton(ActionEvent actionEvent) {
        ServerApi.getInstance().setRequestStatus(requestId.get(), "reject");
    }

    private void approveButton(ActionEvent actionEvent) {
        ServerApi.getInstance().setRequestStatus(requestId.get(), "approve");
    }

    public IntegerProperty requestIdProperty() {
        return requestId;
    }

    public StringProperty requestingUserProperty() {
        return requestingUser;
    }

    public StringProperty worldNameProperty() {
        return worldName;
    }

    public IntegerProperty runAmountProperty() {
        return runAmount;
    }

    public StringProperty ticksTerminationProperty() {
        return ticksTermination;
    }

    public StringProperty secondsTerminationProperty() {
        return secondsTermination;
    }

    public BooleanProperty userTerminationProperty() {
        return userTermination;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public IntegerProperty runsUsedProperty() {
        return runsUsed;
    }


    public IntegerProperty runsCompletedProperty() {
        return runsCompleted;
    }

    public IntegerProperty runsCurrentlyRunningProperty() {
        return runsCurrentlyRunning;
    }
    
    public ObservableValue<Button> approveButtonProperty() {
        return approveButton;
    }

    public ObservableValue<Button> rejectButtonProperty() {
        return rejectButton;
    }
}
