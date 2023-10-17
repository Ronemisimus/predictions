package clientGui.scene.newExecution;

import dto.subdto.requests.RequestDetailsDto;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RequestSelection implements Comparable<RequestSelection>{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestSelection that = (RequestSelection) o;
        return request.getRequestId() == that.request.getRequestId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(request.getRequestId());
    }

    @Override
    public int compareTo(@NotNull RequestSelection o) {
        return request.getRequestId() - o.request.getRequestId();
    }
}
