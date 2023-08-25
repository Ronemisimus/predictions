package console.dto.presenter;

import dto.RunSimulationDto;

public class RunSimulationPresenter extends DTOPresenter {

    private final RunSimulationDto dto;
    public RunSimulationPresenter(RunSimulationDto dto) {
        this.dto = dto;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public String toString() {
        Integer ticks = dto.getTerminationReason().getTicks();
        Integer seconds = dto.getTerminationReason().getSeconds();
        boolean user = dto.getTerminationReason().isUser();
        String terminationStr = (ticks==null?"":"reached tick number " + ticks ) +
                (seconds == null? "" : "reached duration of " + seconds + "seconds") +
                (user? "user terminated the run":"");
        return "run complete\n" +
                "unique id: " + dto.getGuid() + "\ntermination reason: " + terminationStr + "\n\n";
    }
}
