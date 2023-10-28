package main.requests.request;

import dto.subdto.requests.RequestEntryDto;
import main.login.UserManager;
import main.util.EngineApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RequestImpl implements Request {

    private static Integer biggestRequestId = 0;
    private final int requestId;
    private final String worldName;
    private final String requestingUser;
    private final int runAllocation;
    private final Collection<TerminationType> terminationTypes;
    private final Integer tickLimit, secondsLimit;
    private RequestStatus status;

    private final List<Integer> runsRelatedToRequest;

    public RequestImpl(RequestEntryDto request) {
        this.worldName = request.getWorldName();
        this.runAllocation = request.getRunAllocation() == null ? 0 : request.getRunAllocation()>0?request.getRunAllocation():0;
        this.terminationTypes = new ArrayList<>();
        this.runsRelatedToRequest = new ArrayList<>();
        if (request.isUserTermination()) this.terminationTypes.add(TerminationType.USER);
        if (request.getTickLimit()!=null) this.terminationTypes.add(TerminationType.TICKS);
        if (request.getSecondsLimit()!=null) this.terminationTypes.add(TerminationType.SECONDS);
        if (request.getTickLimit()!=null && request.getTickLimit()>0) this.tickLimit = request.getTickLimit();
        else this.tickLimit = null;
        if (request.getSecondsLimit()!=null && request.getSecondsLimit()>0) this.secondsLimit = request.getSecondsLimit();
        else this.secondsLimit = null;
        this.status = RequestStatus.WAITING;
        this.requestingUser = request.getUsername();

        if (isValid()){
            //noinspection SynchronizeOnNonFinalField
            synchronized (biggestRequestId){
                requestId = ++biggestRequestId;
            }
        }
        else {
            requestId = -1;
        }
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public int getRunAllocation() {
        return runAllocation;
    }

    @Override
    public Collection<TerminationType> getTerminationTypes() {
        return terminationTypes;
    }

    @Override
    public Integer getTickLimit() {
        return tickLimit;
    }

    @Override
    public Integer getSecondsLimit() {
        return secondsLimit;
    }

    @Override
    public RequestStatus getStatus() {
        return status;
    }

    @Override
    public RequestStatus approve() {
        RequestStatus prev = this.status;
        if (prev == RequestStatus.WAITING) this.status = RequestStatus.APPROVED_OPEN;
        return prev;
    }

    @Override
    public RequestStatus reject() {
        RequestStatus prev = this.status;
        if (prev == RequestStatus.WAITING) this.status = RequestStatus.REJECTED;
        return prev;
    }

    @Override
    public String requestingUser() {
        return requestingUser;
    }

    @Override
    public boolean isValid() {
        boolean res = worldName != null &&
                EngineApi.getInstance().getLoadedWorlds().contains(worldName);
        if (runAllocation<=0) res = false;
        if(terminationTypes.isEmpty()) res = false;
        if (terminationTypes.contains(TerminationType.TICKS) &&
        tickLimit == null) res = false;
        if (terminationTypes.contains(TerminationType.SECONDS) &&
        secondsLimit == null) res = false;
        if (requestingUser == null) res = false;
        if (res && !UserManager.getInstance().userExists(requestingUser)) res = false;
        return res;
    }

    @Override
    public void addRun(Integer id) {
        runsRelatedToRequest.add(id);
        if (runsRelatedToRequest.size() == runAllocation) {
            status = RequestStatus.APPROVED_CLOSED;
        }
    }

    @Override
    public int getRunsCurrentlyRunning() {
        return Math.toIntExact(
                EngineApi.getInstance().getRunStates().getRunStates().entrySet().stream()
                .filter(run -> (runsRelatedToRequest.contains(run.getKey()) && run.getValue().getRunning() || run.getValue().getPaused()))
                .count());
    }

    @Override
    public int getRunsCompleted() {
        return Math.toIntExact(
                EngineApi.getInstance().getRunStates().getRunStates().entrySet().stream()
                        .filter(run -> (runsRelatedToRequest.contains(run.getKey()) && !run.getValue().getRunning() && !run.getValue().getPaused()))
                        .count());
    }
}
