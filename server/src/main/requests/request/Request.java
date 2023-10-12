package main.requests.request;

import java.util.Collection;

public interface Request {
    int getRequestId();
    String getWorldName();
    int getRunAllocation();
    Collection<TerminationType> getTerminationTypes();
    Integer getTickLimit();
    Integer getSecondsLimit();
    RequestStatus getStatus();
    RequestStatus approve();
    RequestStatus reject();
    String requestingUser();
    boolean isValid();
}
