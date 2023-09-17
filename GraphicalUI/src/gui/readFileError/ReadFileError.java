package gui.readFileError;

import dto.ReadFileDto;
import dto.subdto.read.dto.*;
import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.ActivationErrorDto;
import dto.subdto.read.dto.rule.ExpressionErrorDto;
import dto.subdto.read.dto.rule.RuleErrorDto;
import javafx.scene.control.*;

public class ReadFileError extends Alert {
    private ReadFileError(String readFileDto) {
        super(AlertType.ERROR);
        setTitle("Read File Error");
        setResizable(true);
        setHeaderText(readFileDto);
    }

    public static ReadFileError build(ReadFileDto readFileDto) {
        return new ReadFileError(buildMessage(readFileDto));
    }

    private static String buildMessage(ReadFileDto readFileDto) {
        if (readFileDto.getFileSelectionDto()!=null) {
            return buildMessage(readFileDto.getFileSelectionDto());
        }
        if (!readFileDto.isMatchesSchema())
        {
            return "Schema does not match";
        }
        if(readFileDto.isBadThreadCountError())
        {
            return "Bad thread count";
        }
        if(readFileDto.getGridSizeError())
        {
            return "Grid size error: " + readFileDto.getGridWidth() + "x" + readFileDto.getGridHeight();
        }
        if (readFileDto.getEnvironmentErrorDto()!=null)
        {
            return buildMessage(readFileDto.getEnvironmentErrorDto());
        }
        if (readFileDto.getEntityErrorDto()!=null)
        {
            return buildMessage(readFileDto.getEntityErrorDto());
        }
        if(readFileDto.getRuleErrorDto()!=null)
        {
            return buildMessage(readFileDto.getRuleErrorDto());
        }
        if(readFileDto.getTerminationBadDto()!=null)
        {
            return buildMessage(readFileDto.getTerminationBadDto());
        }
        return "Something went wrong";
    }

    private static String buildMessage(TerminationBadDto terminationBadDto) {
        return "Termination bad error: seconds: " +
                terminationBadDto.getBySecondCount() +
                " ticks: " + terminationBadDto.getByTicksCount();
    }

    private static String buildMessage(RuleErrorDto ruleErrorDto) {
        if (ruleErrorDto.getActivationErrorDto()!=null)
        {
            return buildMessage(ruleErrorDto.getActivationErrorDto());
        }
        if (ruleErrorDto.getExpressionErrorDto()!=null)
        {
            return buildMessage(ruleErrorDto.getExpressionErrorDto());
        }
        if (ruleErrorDto.getActionErrorDto()!=null)
        {
            return buildMessage(ruleErrorDto.getActionErrorDto());
        }
        return "Something went wrong";
    }

    private static String buildMessage(ActionErrorDto actionErrorDto) {
        if (actionErrorDto.getExpressionErrorDto()!=null)
        {
            return buildMessage(actionErrorDto.getExpressionErrorDto());
        }
        if (actionErrorDto.getMissingPropertyInEntityError())
        {
            return "missing property in entity error in action " +
                    actionErrorDto.getActionType() + "missing property " +
                    " property " + actionErrorDto.getProperty();
        }
        if (actionErrorDto.getEntityNotInContext())
        {
            return "entity not in context error in action " +
                    actionErrorDto.getActionType() + " entity " +
                    actionErrorDto.getEntityName();
        }
        if (actionErrorDto.getPropertyTypeMismatch())
        {
            return "property type mismatch error in action " +
                    actionErrorDto.getActionType() + " property " +
                    actionErrorDto.getProperty();
        }
        if(actionErrorDto.getNoPrimaryEntity())
        {
            return "no primary entity error in action " +
                    actionErrorDto.getActionType();
        }
        if(actionErrorDto.getNoEntityNamed())
        {
            return "no entity in action " +
                    actionErrorDto.getActionType() + " named " +
                    actionErrorDto.getEntityName();
        }
        if (actionErrorDto.getBadSecondaryCount())
        {
            return "invalid count for secondary entity in action " +
                    actionErrorDto.getActionType() + " count: " +
                    actionErrorDto.getSecondaryCount();
        }
        return "Something went wrong";
    }

    private static String buildMessage(ExpressionErrorDto expressionErrorDto) {
        if (expressionErrorDto.getFunctionError())
        {
            return "function error in function " +
                    expressionErrorDto.getFunctionName() +
                    " in expression " + expressionErrorDto.getExpression();
        }
        if (expressionErrorDto.getEnvironmentError())
        {
            return "environment error in expression " +
                    expressionErrorDto.getExpression() +
                    " environment variable " +
                    expressionErrorDto.getNotFoundVariable() +
                    " of type " + expressionErrorDto.getPropertyType() +
                    " does not exist";
        }
        if (expressionErrorDto.getRandomTypeError())
        {
            return "random type error in expression " +
                    expressionErrorDto.getExpression() +
                    " random type " + expressionErrorDto.getRandomType();
        }
        if (expressionErrorDto.getRandomParameterError())
        {
            return "random parameter error in expression " +
                    expressionErrorDto.getExpression() +
                    " random parameter " + expressionErrorDto.getRandomParameter();
        }
        if (expressionErrorDto.getEvaluateError())
        {
            return "evaluate error in expression " +
                    expressionErrorDto.getExpression() +
                    " evaluate expression " + expressionErrorDto.getEvaluateExpression();
        }
        if (expressionErrorDto.getMissingEntityInContextError())
        {
            return "missing entity in context error in expression " +
                    expressionErrorDto.getExpression() +
                    " entity " + expressionErrorDto.getMissingEntityInContext();
        }
        if (expressionErrorDto.getMissingPropertyInEntityError())
        {
            return "missing property in entity error in expression " +
                    expressionErrorDto.getExpression() +
                    " entity " + expressionErrorDto.getEntity() +
                    " property " + expressionErrorDto.getMissingPropertyInEntity() +
                    " of type " + expressionErrorDto.getPropertyType();
        }
        if(expressionErrorDto.getPercentTypeError())
        {
            return "percent type error in expression " +
                    expressionErrorDto.getExpression() +
                    " percent type " + expressionErrorDto.getRandomType();
        }
        if (expressionErrorDto.getTicksTypeError())
        {
            return "ticks error in expression " +
                    expressionErrorDto.getExpression() +
                    " ticks cant be used, expected type of expression is: " +
                    expressionErrorDto.getRandomType();
        }
        if (expressionErrorDto.getTicksError())
        {
            return "ticks error in expression " +
                    expressionErrorDto.getExpression() +
                    " can't evaluate ticks of " +
                    expressionErrorDto.getEvaluateExpression();
        }
        if (expressionErrorDto.getBadExpressionType())
        {
            return "bad expression type error in expression " +
                    expressionErrorDto.getExpression() +
                    " expected type of expression is: " +
                    expressionErrorDto.getExpectedType();
        }
        if (expressionErrorDto.getNullExpressionError())
        {
            return "null expression in action";
        }
        return "Something went wrong";
    }

    private static String buildMessage(ActivationErrorDto activationErrorDto) {
        return "Bad activation: ticks: " +
                activationErrorDto.getCycleSizeInTicks() +
                "probability: " +
                activationErrorDto.getProbability();
    }

    private static String buildMessage(EntityErrorDto entityErrorDto) {
        if (entityErrorDto.getRepeatEntityDto()!=null)
        {
            return buildMessage(entityErrorDto.getRepeatEntityDto());
        }
        if(entityErrorDto.getPropertyBadDto()!=null)
        {
            return buildMessage(entityErrorDto.getPropertyBadDto());
        }
        if (entityErrorDto.getRepeatPropertyDto()!=null)
        {
            return buildMessage(entityErrorDto.getRepeatPropertyDto());
        }
        return "Something went wrong";
    }

    private static String buildMessage(RepeatEntityDto repeatEntityDto) {
        return "Repeated entity: " + repeatEntityDto.getName();
    }

    private static String buildMessage(EnvironmentErrorDto environmentErrorDto) {
        if (environmentErrorDto.getRepeatPropertyDto()!=null)
        {
            return buildMessage(environmentErrorDto.getRepeatPropertyDto());
        }
        if (environmentErrorDto.getPropertyBadDto()!=null)
        {
            return buildMessage(environmentErrorDto.getPropertyBadDto());
        }
        return "Something went wrong";
    }

    private static String buildMessage(PropertyBadDto propertyBadDto) {
        if (propertyBadDto.getFlipped())
        {
            return "Flipped range in property: " +
                    propertyBadDto.getPropertyName() +
                    " from " + propertyBadDto.getFrom() + " to " +
                    propertyBadDto.getTo();
        }
        if (propertyBadDto.getBadRangedType())
        {
            return "Bad ranged type in property: " +
                    propertyBadDto.getPropertyName() +
                    "of type " + propertyBadDto.getPropertyType() +
                    " from " + propertyBadDto.getFrom() + " to " +
                    propertyBadDto.getTo();
        }
        if (propertyBadDto.getBadRandom())
        {
            return propertyBadDto.getPropertyName() +
                    ": property of type " +
                    propertyBadDto.getPropertyType() +
                    " can't be random";
        }
        if (propertyBadDto.getValueGivenOnRandom())
        {
            return propertyBadDto.getPropertyName() +
                    ": can't give initial value on random: " +
                    propertyBadDto.getValue();
        }
        if (propertyBadDto.getBadValueType())
        {
            return "Bad value type in property: " +
                    propertyBadDto.getPropertyName() +
                    "of type " + propertyBadDto.getPropertyType() +
                    ": \"" + propertyBadDto.getValue() + "\"";
        }
        if(propertyBadDto.getValueOutOfRange())
        {
            return "Value out of range in property: " +
                    propertyBadDto.getPropertyName() +
                    ": \"" + propertyBadDto.getValue() + "\"" +
                    " not in range from " + propertyBadDto.getFrom() + " to " +
                    propertyBadDto.getTo();
        }
        return "Something went wrong";
    }

    private static String buildMessage(RepeatPropertyDto repeatPropertyDto) {
        return "Repeated key: " +
                repeatPropertyDto.getRepeatedKey() +
                (repeatPropertyDto.getEnvironmentError()?
                        " in environment" :
                        " in Entity context: " +
                                repeatPropertyDto.getEntityContext());
    }

    private static String buildMessage(FileSelectionDto fileSelectionDto) {
        if (fileSelectionDto.isFileExists())
        {
            return "File not found: " + fileSelectionDto.getFilePath();
        }
        if (fileSelectionDto.isFullPathError())
        {
            return "path is not absolute: " + fileSelectionDto.getFilePath();
        }
        if(fileSelectionDto.isFile())
        {
            return "path is directory: " + fileSelectionDto.getFilePath();
        }
        if(fileSelectionDto.isXML())
        {
            return "file is not xml: " + fileSelectionDto.getFilePath();
        }
        return "Something went wrong";
    }
}
