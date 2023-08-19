package dto.subdto.show.world;

public class TerminationDto {
    private final Integer ticks;
    private final Integer seconds;
    private final boolean user;

    public TerminationDto(Integer ticks, Integer seconds, boolean user) {
        this.ticks = ticks;
        this.seconds = seconds;
        this.user = user;
    }

    public Integer getTicks() {
        return ticks;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public boolean isUser() {
        return user;
    }
}
