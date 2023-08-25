package console.dto.presenter;

import dto.subdto.InitializeDto;

import java.util.stream.Collectors;

public class InitializePresenter extends DTOPresenter {
    private final InitializeDto initDto;
    public InitializePresenter(InitializeDto initDto) {
        this.initDto = initDto;
    }


    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        return "environment variable values:\n" +
        initDto.getEnvironment().stream()
                .map(propertyInstanceDto -> "variable " +
                propertyInstanceDto.getDef().getName()
                + " of type: "+ propertyInstanceDto.getDef().getType() +
                " has initial value of " + propertyInstanceDto.getValue())
                .collect(Collectors.joining("\n")) + "\n";
    }
}
