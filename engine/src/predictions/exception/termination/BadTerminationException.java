package predictions.exception.termination;

import dto.ReadFileDto;
import dto.subdto.read.dto.TerminationBadDto;

public class BadTerminationException extends Exception{
    private final String bySecondCount;
    private final String byTicksCount;

    public BadTerminationException(String bySecondCount, String byTicksCount){
        super();
        this.bySecondCount = bySecondCount;
        this.byTicksCount = byTicksCount;
    }

    public ReadFileDto getReadFileDto(){
        return new ReadFileDto.Builder().terminationError(new TerminationBadDto(Integer.parseInt(bySecondCount), Integer.parseInt(byTicksCount))).build();
    }
}
