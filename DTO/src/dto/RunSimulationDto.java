package dto;

import dto.subdto.show.world.TerminationDto;

public class RunSimulationDto implements DTO {
    private final int guid;
    private final TerminationDto terminationReason;

    public RunSimulationDto(int guid, TerminationDto terminationReason) {
        this.guid = guid;
        this.terminationReason = terminationReason;
    }

    public int getGuid() {
        return guid;
    }

    public TerminationDto getTerminationReason() {
        return terminationReason;
    }
}
