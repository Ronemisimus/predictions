package dto.subdto.requests;

public class RequestEntryDto {
    private final String username;
    private final String worldName;
    private final Integer runAllocation;
    private final Integer tickLimit;
    private final Integer secondsLimit;
    private final boolean userTermination;

    public RequestEntryDto(String username, String worldName, Integer runAllocation, Integer tickLimit, Integer secondsLimit, boolean userTermination) {
        this.username = username;
        this.worldName = worldName;
        this.runAllocation = runAllocation;
        this.tickLimit = tickLimit;
        this.secondsLimit = secondsLimit;
        this.userTermination = userTermination;
    }

    public String getUsername() {
        return username;
    }

    public String getWorldName() {
        return worldName;
    }

    public Integer getRunAllocation() {
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
}
