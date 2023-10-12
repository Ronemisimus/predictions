package dto.subdto.requests;

public class RequestDetailsDto {
    int requestId;
    String worldName;
    String username;
    int runAllocation;
    Integer tickLimit;
    Integer secondsLimit;
    boolean userTermination;
    String status;
    int runsUsed;
    int runsCompleted;
    int runsCurrentlyRunning;

    public RequestDetailsDto(Builder builder) {
        this.requestId = builder.requestId;
        this.worldName = builder.worldName;
        this.username = builder.username;
        this.runAllocation = builder.runAllocation;
        this.tickLimit = builder.tickLimit;
        this.secondsLimit = builder.secondsLimit;
        this.userTermination = builder.userTermination;
        this.status = builder.status;
        this.runsUsed = builder.runsUsed;
        this.runsCompleted = builder.runsCompleted;
        this.runsCurrentlyRunning = builder.runsCurrentlyRunning;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getUsername() {
        return username;
    }

    public int getRunAllocation() {
        return runAllocation;
    }

    public Integer getTickLimit() {
        return tickLimit;
    }

    public Integer getSecondsLimit() {
        return secondsLimit;
    }

    public boolean isUserTermination() {
        return userTermination;
    }

    public String getStatus() {
        return status;
    }

    public int getRunsUsed() {
        return runsUsed;
    }

    public int getRunsCompleted() {
        return runsCompleted;
    }

    public int getRunsCurrentlyRunning() {
        return runsCurrentlyRunning;
    }

    // Builder pattern
    public static class Builder {
        private int requestId;
        private String worldName;
        private String username;
        private int runAllocation;
        private Integer tickLimit;
        private Integer secondsLimit;
        private boolean userTermination;
        private String status;
        private int runsUsed;
        private int runsCompleted;
        private int runsCurrentlyRunning;

        public Builder() {}

        public RequestDetailsDto build() {
            return new RequestDetailsDto(this);
        }

        public Builder requestId(int requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder worldName(String worldName) {
            this.worldName = worldName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder runAllocation(int runAllocation) {
            this.runAllocation = runAllocation;
            return this;
        }

        public Builder tickLimit(Integer tickLimit) {
            this.tickLimit = tickLimit;
            return this;
        }

        public Builder secondsLimit(Integer secondsLimit) {
            this.secondsLimit = secondsLimit;
            return this;
        }

        public Builder userTermination(boolean userTermination) {
            this.userTermination = userTermination;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder runsUsed(int runsUsed) {
            this.runsUsed = runsUsed;
            return this;
        }

        public Builder runsCompleted(int runsCompleted) {
            this.runsCompleted = runsCompleted;
            return this;
        }

        public Builder runsCurrentlyRunning(int runsCurrentlyRunning) {
            this.runsCurrentlyRunning = runsCurrentlyRunning;
            return this;
        }
    }
}
