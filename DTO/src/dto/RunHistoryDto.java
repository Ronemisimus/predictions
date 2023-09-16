package dto;

import dto.subdto.show.instance.RunStateDto;

import java.time.LocalDateTime;
import java.util.Map;

public class RunHistoryDto implements DTO {

    private final Map<Integer, LocalDateTime> runList;
    private final Map<Integer, RunStateDto> runStates;

    public RunHistoryDto(Map<Integer, LocalDateTime> runList, Map<Integer, RunStateDto> runStates) {
        this.runList = runList;
        this.runStates = runStates;
    }

    public Map<Integer, LocalDateTime> getRunList() {
        return runList;
    }

    public Map<Integer, RunStateDto> getRunStates() {
        return runStates;
    }
}
