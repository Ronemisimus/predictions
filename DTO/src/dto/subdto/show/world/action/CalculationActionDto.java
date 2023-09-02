package dto.subdto.show.world.action;

import dto.subdto.show.world.EntityDto;

public class CalculationActionDto extends ActionDto{

    private final String resultPropName;
    private final String calculationExpression;

    public CalculationActionDto(EntityDto primaryEntity,
                                EntityDto secondaryEntity,
                                String resultPropName,
                                String calculationExpression) {
        super("Calculation", primaryEntity, secondaryEntity);
        this.resultPropName = resultPropName;
        this.calculationExpression = calculationExpression;
    }

    public String getResultPropName() {
        return resultPropName;
    }

    public String getCalculationExpression() {
        return calculationExpression;
    }
}
