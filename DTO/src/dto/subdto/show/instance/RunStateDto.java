package dto.subdto.show.instance;

import dto.DTO;

public class RunStateDto implements DTO {
    private final Boolean running;
    private final Boolean paused;
    private final Boolean finished;
    private final Boolean stopped;

    public RunStateDto(Boolean running, Boolean paused, Boolean finished, Boolean stopped) {
        this.running = running;
        this.paused = paused;
        this.finished = finished;
        this.stopped = stopped;
    }

    public Boolean getRunning() {
        return running;
    }

    public Boolean getPaused() {
        return paused;
    }

    public Boolean getFinished() {
        return finished;
    }

    public Boolean getStopped() {
        return stopped;
    }
}
