package dto;

import java.time.LocalDateTime;
import java.util.Map;

public class RunHistoryDto implements DTO {

    private final Map<Integer, LocalDateTime> runList;

    public RunHistoryDto(Map<Integer, LocalDateTime> runList) {
        this.runList = runList;
    }

    public Map<Integer, LocalDateTime> getRunList() {
        return runList;
    }
}
