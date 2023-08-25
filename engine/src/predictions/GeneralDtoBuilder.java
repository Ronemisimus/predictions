package predictions;

import dto.ReadFileDto;
import dto.subdto.SingleRunHistoryDto;
import predictions.exception.*;
import predictions.execution.EntityCountHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralDtoBuilder {
    public static SingleRunHistoryDto buildSingleRunDtoEntity(Map<String, EntityCountHistory> res) {
        List<String> entities = new ArrayList<>(res.keySet());
        List<Integer> startCount = entities.stream().map(res::get)
                .map(EntityCountHistory::getInitialCount)
                .collect(Collectors.toList());
        List<Integer> finalCount = entities.stream().map(res::get)
                .map(EntityCountHistory::getEndCount)
                .collect(Collectors.toList());
        return new SingleRunHistoryDto(entities, startCount,finalCount, null);
    }

    public static SingleRunHistoryDto buildSingleRunDtoProperty(Map<Comparable<?>, Integer> propertyHist) {
        return new SingleRunHistoryDto(null,null,null,propertyHist);
    }

    public static ReadFileDto getReadFileDtoBasic(boolean absolutePathError, boolean fileDoesNotExist, boolean isNotFile, boolean isNotXML) {
        return new ReadFileDto(absolutePathError,
                fileDoesNotExist,
                isNotFile,
                isNotXML,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null);
    }

    public static ReadFileDto getReadFileDtoJAXB() {
        return new ReadFileDto(false,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null);
    }

    public static ReadFileDto getReadFileDtoRepeatName(boolean environmentVariable, String variableName, String entityName) {
        return new ReadFileDto(false,
                false,
                false,
                false,
                false,
                true,
                false,
                environmentVariable,
                false,
                false,
                false,
                false,
                false,
                false,
                variableName,
                entityName,
                null,
                null,
                null);
    }

    public static ReadFileDto getReadFileDtoUnknown() {
        return new ReadFileDto(false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null);
    }

    public static ReadFileDto getReadFileDtoSuccess() {
        return new ReadFileDto(false,
                false,
                false,
                false,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null);
    }

    public static ReadFileDto getReadFileDtoException(Throwable cause) {
        if (cause instanceof MissingPropertyExpressionException)
        {
            MissingPropertyExpressionException exp = (MissingPropertyExpressionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    exp.isEnvironment(),
                    false,
                    false,
                    exp.getFinalExpression(),
                    exp.getEntityDefinition().getName(),
                    null,
                    null,
                    null);
        } else if (cause instanceof MissingPropertyActionException) {
            MissingPropertyActionException exp = (MissingPropertyActionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                    exp.getProperty(),
                    null,
                    null,
                    exp.getActionType().name(),
                    null);
        }
        else if (cause instanceof BadPropertyTypeExpressionException)
        {
            BadPropertyTypeExpressionException exp = (BadPropertyTypeExpressionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    false,
                    false,
                    exp.getExpression(),
                    null,
                    null,
                    null,
                    exp.getType().name());
        }
        else if (cause instanceof BadFunctionExpressionException)
        {
            BadFunctionExpressionException exp = (BadFunctionExpressionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    exp.getExpression(),
                    null,
                    null,
                    null,
                    null);
        }
        else if(cause instanceof BadExpressionException)
        {
            BadExpressionException exp = (BadExpressionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    exp.getExpression(),
                    null,
                    null,
                    null,
                    null);
        }
        else if (cause instanceof NoSuchEntityActionException) {
            NoSuchEntityActionException exp = (NoSuchEntityActionException) cause;
            return new ReadFileDto(false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    null,
                    exp.getEntity(),
                    null,
                    null,
                    null);
        }
        else {
            return getReadFileDtoUnknown();
        }
    }
}
