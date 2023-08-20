package predictions.exception;

public class NoSuchEntityActionException extends Throwable {
    private final String entity;

    public NoSuchEntityActionException(String entity) {
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }
}
