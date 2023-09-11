package dto.subdto.read.dto;

import dto.DTO;

public class TerminationBadDto implements DTO {
    private final Integer bySecondCount;
    private final Integer byTicksCount;

    public TerminationBadDto(Integer bySecondCount, Integer byTicksCount){
        this.bySecondCount = bySecondCount;
        this.byTicksCount = byTicksCount;
    }

    public Integer getBySecondCount() {
        return bySecondCount;
    }

    public Integer getByTicksCount() {
        return byTicksCount;
    }
}
