package predictions.execution;

public class EntityCountHistory {
    private int initialCount;
    private int endCount;

    public EntityCountHistory(int initialCount, int endCount) {
        this.initialCount = initialCount;
        this.endCount = endCount;
    }

    public int getInitialCount() {
        return initialCount;
    }

    public int getEndCount() {
        return endCount;
    }

    public void setInitialCount(int initialCount) {
        this.initialCount = initialCount;
    }

    public void setEndCount(int endCount) {
        this.endCount = endCount;
    }
}
