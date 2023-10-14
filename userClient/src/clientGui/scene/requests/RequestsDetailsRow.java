package clientGui.scene.requests;

import dto.subdto.requests.RequestDetailsDto;
import javafx.beans.property.*;

public class RequestsDetailsRow {
    private final IntegerProperty requestId;
    private final StringProperty worldName;
    private final IntegerProperty runAmount;
    private final StringProperty ticksTermination;
    private final StringProperty secondsTermination;
    private final BooleanProperty userTermination;
    private final StringProperty status;
    private final IntegerProperty runsUsed;
    private final IntegerProperty runsCompleted;
    private final IntegerProperty runsCurrentlyRunning;

    public RequestsDetailsRow(RequestDetailsDto requestDetailsDto) {
        requestId = new SimpleIntegerProperty();
        worldName = new SimpleStringProperty();
        runAmount = new SimpleIntegerProperty();
        ticksTermination = new SimpleStringProperty();
        secondsTermination = new SimpleStringProperty();
        userTermination = new SimpleBooleanProperty();
        status = new SimpleStringProperty();
        runsUsed = new SimpleIntegerProperty();
        runsCompleted = new SimpleIntegerProperty();
        runsCurrentlyRunning = new SimpleIntegerProperty();

        requestId.set(requestDetailsDto.getRequestId());
        worldName.set(requestDetailsDto.getWorldName());
        runAmount.set(requestDetailsDto.getRunAllocation());
        ticksTermination.set(requestDetailsDto.getTickLimit()==null?"-":requestDetailsDto.getTickLimit().toString());
        secondsTermination.set(requestDetailsDto.getSecondsLimit()==null?"-":requestDetailsDto.getSecondsLimit().toString());
        userTermination.set(requestDetailsDto.isUserTermination());
        status.set(requestDetailsDto.getStatus());
        runsUsed.set(requestDetailsDto.getRunsUsed());
        runsCompleted.set(requestDetailsDto.getRunsCompleted());
        runsCurrentlyRunning.set(requestDetailsDto.getRunsCurrentlyRunning());
    }

    public int getRequestId() {
        return requestId.get();
    }

    public IntegerProperty requestIdProperty() {
        return requestId;
    }

    public String getWorldName() {
        return worldName.get();
    }

    public StringProperty worldNameProperty() {
        return worldName;
    }

    public int getRunAmount() {
        return runAmount.get();
    }

    public IntegerProperty runAmountProperty() {
        return runAmount;
    }

    public String getTicksTermination() {
        return ticksTermination.get();
    }

    public StringProperty ticksTerminationProperty() {
        return ticksTermination;
    }

    public String getSecondsTermination() {
        return secondsTermination.get();
    }

    public StringProperty secondsTerminationProperty() {
        return secondsTermination;
    }

    public boolean isUserTermination() {
        return userTermination.get();
    }

    public BooleanProperty userTerminationProperty() {
        return userTermination;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public int getRunsUsed() {
        return runsUsed.get();
    }

    public IntegerProperty runsUsedProperty() {
        return runsUsed;
    }

    public int getRunsCompleted() {
        return runsCompleted.get();
    }

    public IntegerProperty runsCompletedProperty() {
        return runsCompleted;
    }

    public int getRunsCurrentlyRunning() {
        return runsCurrentlyRunning.get();
    }

    public IntegerProperty runsCurrentlyRunningProperty() {
        return runsCurrentlyRunning;
    }
}
