package clientGui.scene.newExecution;

import dto.subdto.requests.RequestDetailsDto;

public class RequestSelection {
    private final RequestDetailsDto request;
    public RequestSelection(RequestDetailsDto request) {
        this.request = request;
    }

    public int getRequestId() {
        return request.getRequestId();
    }

    public String getWorldName() {
        return request.getWorldName();
    }

    @Override
    public String toString() {
        return getRequestId() +
                " - " + getWorldName();
    }
}
