package predictions.expression;

import predictions.action.api.ContextDefinition;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.api.PropertyType;
import predictions.exception.BadExpressionException;
import predictions.exception.BadFunctionExpressionException;
import predictions.exception.BadPropertyTypeExpressionException;
import predictions.exception.MissingPropertyExpressionException;
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
    public static Expression<Double> buildDoubleExpression(String expression, ContextDefinition contextDefinition) throws BadFunctionExpressionException, MissingPropertyExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        Expression<Double> funcExpression = (Expression<Double>) buildFunctionExpression(expression, contextDefinition, PropertyType.FLOAT);
        if (funcExpression != null) return funcExpression;
        Expression<Double> entPropertyExpression = (Expression<Double>) buildEntityPropertyExpression(expression, contextDefinition, PropertyType.FLOAT);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleDoubleExpression(expression);
    }

    private static Expression<Double> buildSimpleDoubleExpression(String expression) throws BadPropertyTypeExpressionException {
        try {
            Double res = Double.parseDouble(expression);
            return new NumberExpression(res);
        }catch (NumberFormatException e)
        {
            throw new BadPropertyTypeExpressionException(expression, PropertyType.DECIMAL);
        }
    }

    private static Expression<?> buildEntityPropertyExpression(String expression, ContextDefinition contextDefinition, PropertyType type) throws BadPropertyTypeExpressionException {
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

    public static Expression<?> buildFunctionExpression(String expression, ContextDefinition contextDefinition, PropertyType type) throws BadFunctionExpressionException, MissingPropertyExpressionException, BadExpressionException, BadPropertyTypeExpressionException {
        String finalExpression = expression;
        Optional<String> func = functions.stream().filter(f -> finalExpression.toLowerCase().startsWith(f)).findFirst();
        if(!func.isPresent()) return null;
        String funcName = func.get();
        expression = expression.substring(funcName.length());
        if (!expression.startsWith("(") || !expression.endsWith(")")) throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
        expression = expression.substring(1, expression.length()-1);

        String prop;
        String entity;
        Optional<String> propDef;
        List<EntityDefinition> entities = new ArrayList<>();
        entities.add(contextDefinition.getSecondaryEntityDefinition());
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
                        .orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, contextDefinition, true, type));
                return new EnviromentExpression(expression);
            case "random":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL)
                    throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
                try {
                    return new RandomExpression(Integer.parseInt(expression));
                }catch (NumberFormatException e)
                {
                    throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
                }
            case "evaluate":
                prop = expression.substring(expression.indexOf(".")+1);
                entity = expression.substring(0, expression.indexOf("."));

                EntityDefinition entityDefinition = entities.stream()
                        .filter(e -> e.getName().equals(entity))
                        .findFirst().orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, contextDefinition, false, type));

                propDef = entityDefinition.getProps().stream()
                        .filter(p->p.getType().equals(type))
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new PropertyExpression<>(entityDefinition, prop);
                }
                else
                {
                    throw new MissingPropertyExpressionException(finalExpression, contextDefinition, false, type);
                }
            case "percent":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL)
                    throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
                return new DualMathExpression(MathOperation.PERCENT,
                        buildDoubleExpression(expression.substring(0,expression.indexOf(",")), contextDefinition),
                        buildDoubleExpression(expression.substring(expression.indexOf(",")+1), contextDefinition)
                );
            case "ticks":
                if (type != PropertyType.FLOAT && type != PropertyType.DECIMAL)
                    throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
                entity = expression.substring(0, expression.indexOf("."));
                prop = expression.substring(expression.indexOf(".") + 1);

                EntityDefinition selectedEntity = entities.stream()
                        .filter(e -> e.getName().equals(entity))
                        .findFirst().orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, contextDefinition, false, type));

                propDef = selectedEntity.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new TicksExpression(prop);
                }
                else {
                    throw new MissingPropertyExpressionException(finalExpression, contextDefinition, false, type);
                }
        }
        throw new BadFunctionExpressionException(finalExpression, contextDefinition, type);
    }

    public static Expression<String> buildStringExpression(String valueExpression, ContextDefinition contextDefinition) throws MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        Expression<String> funcExpression = (Expression<String>) buildFunctionExpression(valueExpression, contextDefinition, PropertyType.STRING);
        if (funcExpression != null) return funcExpression;
        Expression<String> entPropertyExpression = (Expression<String>) buildEntityPropertyExpression(valueExpression, contextDefinition, PropertyType.STRING);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleStringExpression(valueExpression);
    }

    private static Expression<String> buildSimpleStringExpression(String valueExpression) {
        return context -> valueExpression;
    }

    public static Expression<Boolean> buildBooleanExpression(String valueExpression,
                                                             ContextDefinition contextDefinition) throws MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        Expression<Boolean> funcExpression = (Expression<Boolean>) buildFunctionExpression(valueExpression, contextDefinition, PropertyType.BOOLEAN);
        if (funcExpression != null) return funcExpression;
        Expression<Boolean> entPropertyExpression = (Expression<Boolean>) buildEntityPropertyExpression(valueExpression, contextDefinition, PropertyType.BOOLEAN);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleBooleanExpression(valueExpression);
    }

    public static Expression<?> buildGenericExpression(String valueExpression, ContextDefinition contextDefinition) throws MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        try {
            return buildDoubleExpression(valueExpression, contextDefinition);
        }catch (BadPropertyTypeExpressionException | MissingPropertyExpressionException e)
        {
            try {
                return buildBooleanExpression(valueExpression, contextDefinition);
            }catch (BadPropertyTypeExpressionException | MissingPropertyExpressionException e1) {
                try {
                    return buildStringExpression(valueExpression, contextDefinition);
                }catch (BadPropertyTypeExpressionException | MissingPropertyExpressionException e2)
                {
                    throw e2;
                }
            }

        }
    }

    private static Expression<Boolean> buildSimpleBooleanExpression(String valueExpression) throws BadPropertyTypeExpressionException {
        Boolean val = valueExpression.equalsIgnoreCase("true");
        if (!val && !valueExpression.equalsIgnoreCase("false"))
            throw new BadPropertyTypeExpressionException(valueExpression, PropertyType.BOOLEAN);
        return context -> {
            try {
                return val;
            } catch (NumberFormatException e) {
                throw new RuntimeException();
            }
        };
    }

}
