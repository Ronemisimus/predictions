package dto.subdto.show.interactive;

import dto.DTO;

import java.time.Duration;

public class RunProgressDto implements DTO {
    private final Integer tick;
    private final Integer maxTick;

    private final Duration second;
    private final Duration maxSecond;

    private final String status;

    public RunProgressDto(Integer tick, Integer maxTick, Duration second, Duration maxSecond, String status) {
        this.tick = tick;
        this.maxTick = maxTick;
        this.second = second;
        this.maxSecond = maxSecond;
        this.status = status;
    }

    public Integer getTick() {
        return tick;
    }

    public Integer getMaxTick() {
        return maxTick;
    }

    public Duration getSecond() {
        return second;
    }

    public Duration getMaxSecond() {
        return maxSecond;
    }

    public String getStatus() {
        return status;
    }
}
