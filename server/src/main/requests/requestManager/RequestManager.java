package main.requests.requestManager;

import dto.subdto.requests.RequestDetailsDto;
import dto.subdto.requests.RequestEntryDto;
import main.requests.request.Request;
import main.requests.request.TerminationType;

import java.util.Collection;

public interface RequestManager {
    Request getRequest(int requestId);
    Request addRequest(RequestEntryDto request);
    default RequestDetailsDto getRequestDetails(int requestId){
        Request request = getRequest(requestId);

        RequestDetailsDto.Builder builder = new RequestDetailsDto.Builder();

        builder = builder.requestId(request.getRequestId())
                .worldName(request.getWorldName())
                .username(request.requestingUser())
                .runAllocation(request.getRunAllocation())
                .tickLimit(request.getTerminationTypes()
                        .contains(TerminationType.TICKS)?
                        request.getTickLimit():null
                )
                .secondsLimit(request.getTerminationTypes()
                        .contains(TerminationType.SECONDS)?
                        request.getSecondsLimit():null
                )
                .userTermination(request.getTerminationTypes()
                        .contains(TerminationType.USER)
                )
                .status(request.getStatus().name())
                .runsUsed(getTotalRunsUsed(requestId))
                .runsCompleted(getRunsCompleted(requestId))
                .runsCurrentlyRunning(getRunsCurrentlyRunning(requestId));
        return builder.build();
    }

    Collection<RequestDetailsDto> getRequests();

    Collection<RequestDetailsDto> getRequestsByUser(String username);

    int getRunsCurrentlyRunning(int requestId);

    int getRunsCompleted(int requestId);

    default int getTotalRunsUsed(int requestId){
        return getRunsCompleted(requestId) + getRunsCurrentlyRunning(requestId);
    }

    static RequestManager getInstance(){
        return RequestManagerImpl.getInstance();
    }
}
