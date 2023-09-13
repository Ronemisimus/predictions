package predictions.expression;

import dto.subdto.read.dto.rule.ExpressionErrorDto;
import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
import predictions.execution.context.Context;
import predictions.expression.api.Expression;
import predictions.expression.api.MathOperation;
import predictions.expression.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ExpressionBuilder {

    private static final List<String> functions;

    static {
        functions = new ArrayList<>();
        functions.add("environment");
        functions.add("random");
        functions.add("evaluate");
        functions.add("percent");
        functions.add("ticks");
    }
    public static Expression<Double> buildDoubleExpression(String expression,
                                                           ContextDefinition contextDefinition,
                                                            ExpressionErrorDto.Builder builder) {
        Expression<Double> funcExpression = (Expression<Double>) buildFunctionExpression(expression, contextDefinition, PropertyType.FLOAT, builder);
        if (funcExpression != null) return funcExpression;
        Expression<Double> entPropertyExpression = (Expression<Double>) buildEntityPropertyExpression(expression, contextDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleDoubleExpression(expression, builder);
    }

    private static Expression<Double> buildSimpleDoubleExpression(String expression,
                                                                    ExpressionErrorDto.Builder builder) {
        try {
            Double res = Double.parseDouble(expression);
            return new NumberExpression(res);
        }catch (NumberFormatException e)
        {
            builder.withExpression(expression)
                    .badExpressionType("float");
            throw new RuntimeException("not a number: " + expression, e);
        }
    }

    private static Expression<?> buildEntityPropertyExpression(String expression,
                                                               ContextDefinition contextDefinition) {
        Optional<PropertyDefinition<?>> propDef = contextDefinition.getPrimaryEntityDefinition().getProps().stream()
                .filter(e -> e.getName().equals(expression)).findFirst();
        Optional<PropertyDefinition<?>> propSecondary = contextDefinition.getSecondaryEntityDefinition() == null?
                Optional.empty() :
                contextDefinition.getSecondaryEntityDefinition().getProps().stream()
                .filter(e -> e.getName().equals(expression)).findFirst();
        if (propDef.isPresent())
                return new PropertyExpression<>(contextDefinition.getPrimaryEntityDefinition(), expression);
        else if (propSecondary.isPresent())
                return new PropertyExpression<>(contextDefinition.getSecondaryEntityDefinition(), expression);
        else
            return null;
    }

    public static Expression<?> buildFunctionExpression(String expression,
                                                        ContextDefinition contextDefinition,
                                                        PropertyType type,
                                                        ExpressionErrorDto.Builder builder) {
        String finalExpression = expression;
        Optional<String> func = functions.stream().filter(f -> finalExpression.toLowerCase().startsWith(f)).findFirst();
        if(!func.isPresent()) return null;
        String funcName = func.get();
        expression = expression.substring(funcName.length());
        if (!expression.startsWith("(") || !expression.endsWith(")"))
        {
            builder.withExpression(finalExpression).withFunctionName(funcName);
            throw new RuntimeException("bad function expression");
        }
        expression = expression.substring(1, expression.length()-1);

        String prop;
        String entity;
        Optional<String> propDef;
        List<EntityDefinition> entities = new ArrayList<>();
        if (contextDefinition.getSecondaryEntityDefinition()!=null)
            entities.add(contextDefinition.getSecondaryEntityDefinition());
        if (contextDefinition.getPrimaryEntityDefinition()!=null)
            entities.add(contextDefinition.getPrimaryEntityDefinition());
        switch (funcName)
        {
            case "environment":
                String finalExpression1 = expression;
                contextDefinition.getEnvVariables()
                        .getEnvVariables().stream()
                        .filter(p -> p.getType() == type)
                        .map(p->p.getName())
                        .filter(name -> name.equals(finalExpression1))
                        .findFirst()
                        .orElseThrow(() -> {
                            builder.withExpression(finalExpression)
                                    .environmentError(finalExpression1, type.name());
                            return new RuntimeException("bad environment expression");
                        });
                return new EnviromentExpression(expression);
            case "random":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL) {
                    builder.withExpression(finalExpression)
                            .randomTypeError(type.name());
                    throw new RuntimeException("random expression where " + type.name() + " expected");
                }
                Integer finalInt = null;
                try{
                    finalInt = Integer.parseInt(expression);
                }catch (NumberFormatException ignored) {}

                if (finalInt == null || finalInt < 0)
                {
                    builder.withExpression(finalExpression)
                            .randomParameterError(expression);
                    throw new RuntimeException("random expression parameter " + expression + " is not a whole number bigger then 0");
                }
                else
                {
                    return new RandomExpression(finalInt);
                }
            case "evaluate":
                if (!expression.contains("."))
                {
                    builder.withExpression(finalExpression)
                            .evaluateError(expression);
                    throw new RuntimeException("evaluate expression with no dot");
                }
                prop = expression.substring(expression.indexOf(".")+1);
                entity = expression.substring(0, expression.indexOf("."));

                EntityDefinition entityDefinition = entities.stream()
                        .filter(e -> e.getName().equals(entity))
                        .findFirst().orElseThrow(() -> {
                            builder.withExpression(finalExpression)
                                    .missingEntityInContextError(entity);
                            return new RuntimeException("missing entity in context of evaluate expression");
                        });

                propDef = entityDefinition.getProps().stream()
                        .filter(p->p.getType().equals(type))
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new PropertyExpression<>(entityDefinition, prop);
                }
                else
                {
                    builder.withExpression(finalExpression)
                            .missingPropertyInEntityError(entity, prop, type.name());
                    throw new RuntimeException("missing property in entity of evaluate expression");
                }
            case "percent":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL) {
                    builder.withExpression(finalExpression)
                            .percentTypeError(type.name());
                    throw new RuntimeException("percent expression where " + type.name() + " expected");
                }
                return new DualMathExpression(MathOperation.PERCENT,
                        buildDoubleExpression(expression.substring(0,expression.indexOf(",")), contextDefinition, builder),
                        buildDoubleExpression(expression.substring(expression.indexOf(",")+1), contextDefinition, builder));
            case "ticks":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL) {
                    builder.withExpression(finalExpression)
                            .ticksTypeError(type.name());
                    throw new RuntimeException("ticks expression where " + type.name() + " expected");
                }
                if (!expression.contains("."))
                {
                    builder.withExpression(finalExpression)
                            .ticksError(expression);
                    throw new RuntimeException("ticks expression with no dot");
                }
                entity = expression.substring(0, expression.indexOf("."));
                prop = expression.substring(expression.indexOf(".") + 1);

                EntityDefinition selectedEntity = entities.stream()
                        .filter(e -> e.getName().equals(entity))
                        .findFirst().orElseThrow(() -> {
                            builder.withExpression(finalExpression)
                                    .missingEntityInContextError(entity);
                            return new RuntimeException("missing entity in context of ticks expression");
                        });

                propDef = selectedEntity.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new TicksExpression(prop, selectedEntity);
                }
                else {
                    builder.withExpression(finalExpression)
                            .missingPropertyInEntityError(entity, prop, type.name());
                    throw new RuntimeException("missing property in entity of ticks expression");
                }
        }
        throw new RuntimeException("bad function expression");
    }

    public static Expression<String> buildStringExpression(String valueExpression,
                                                           ContextDefinition contextDefinition,
                                                           ExpressionErrorDto.Builder builder) {
        Expression<String> funcExpression = (Expression<String>) buildFunctionExpression(valueExpression, contextDefinition, PropertyType.STRING, builder);
        if (funcExpression != null) return funcExpression;
        Expression<String> entPropertyExpression = (Expression<String>) buildEntityPropertyExpression(valueExpression, contextDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleStringExpression(valueExpression);
    }

    private static Expression<String> buildSimpleStringExpression(String valueExpression) {
        return new Expression<String>() {
            @Override
            public Comparable<String> evaluate(Context context) {
                return valueExpression;
            }
            @Override
            public String toString() {
                return valueExpression;
            }
        };
    }

    public static Expression<Boolean> buildBooleanExpression(String valueExpression,
                                                             ContextDefinition contextDefinition,
                                                             ExpressionErrorDto.Builder builder) {
        Expression<Boolean> funcExpression = (Expression<Boolean>) buildFunctionExpression(valueExpression, contextDefinition, PropertyType.BOOLEAN, builder);
        if (funcExpression != null) return funcExpression;
        Expression<Boolean> entPropertyExpression = (Expression<Boolean>) buildEntityPropertyExpression(valueExpression, contextDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleBooleanExpression(valueExpression);
    }

    public static Expression<?> buildGenericExpression(String valueExpression,
                                                       ContextDefinition contextDefinition,
                                                       ExpressionErrorDto.Builder builder) {
        try {
            return buildDoubleExpression(valueExpression, contextDefinition, builder);
        }catch (Exception e)
        {
            try {
                return buildBooleanExpression(valueExpression, contextDefinition, builder);
            }catch (Exception e1) {
                try {
                    return buildStringExpression(valueExpression, contextDefinition, builder);
                }catch (Exception e2)
                {
                    throw e2;
                }
            }
        }
    }

    private static Expression<Boolean> buildSimpleBooleanExpression(String valueExpression) {
        Boolean val = valueExpression.equalsIgnoreCase("true");
        if (!val && !valueExpression.equalsIgnoreCase("false"))
            throw new RuntimeException("not a boolean: " + valueExpression);
        return new BasicBooleanExpression(val);
    }

}
