package main.requests.requestManager;

import dto.subdto.requests.RequestDetailsDto;
import dto.subdto.requests.RequestEntryDto;
import main.requests.request.Request;
import main.requests.request.RequestImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestManagerImpl implements RequestManager {

    private static final RequestManagerImpl instance = new RequestManagerImpl();

    private final Map<Integer, Request> requests;
    private final Map<Integer, Integer> completedRuns;

    private final Map<Integer,Integer> runsCurrentlyRunning;

    private RequestManagerImpl(){
        requests = new HashMap<>();
        completedRuns = new HashMap<>();
        runsCurrentlyRunning = new HashMap<>();
    }

    public static synchronized RequestManagerImpl getInstance(){
        return instance;
    }

    @Override
    public synchronized Request getRequest(int requestId) {
        return requests.getOrDefault(requestId, null);
    }

    @Override
    public synchronized Request addRequest(RequestEntryDto request) {
        Request req = new RequestImpl(request);
        requests.put(req.getRequestId(), req);
        completedRuns.put(req.getRequestId(),0);
        runsCurrentlyRunning.put(req.getRequestId(),0);
        return req;
    }

    @Override
    public synchronized Collection<RequestDetailsDto> getRequests() {
        Collection<Request> requests;
        synchronized (this) {
            requests = new ArrayList<>(this.requests.values());
        }
        return requests.stream()
                .map(req -> this.getRequestDetails(req.getRequestId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<RequestDetailsDto> getRequestsByUser(String username) {
        Collection<Request> requests;
        synchronized (this) {
            requests = new ArrayList<>(this.requests.values());
        }
        return requests.stream()
                .filter(request -> request.requestingUser().equals(username))
                .map(req -> this.getRequestDetails(req.getRequestId()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized int getRunsCurrentlyRunning(int requestId) {
        return runsCurrentlyRunning.get(requestId);
    }

    @Override
    public synchronized int getRunsCompleted(int requestId) {
        return completedRuns.get(requestId);
    }
}
