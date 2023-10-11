package clientGui.history.data;

import dto.subdto.show.instance.RunStateDto;

public enum RunState {
    RUNNING, FINISHED, STOPPED, PAUSED,WAITING;

    public static RunState getRunState(RunStateDto dto) {
        return dto.getRunning() ? RUNNING : dto.getFinished() ? FINISHED : dto.getStopped() ? STOPPED : dto.getPaused() ? PAUSED : WAITING;
    }
}
