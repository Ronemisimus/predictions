package predictions.expression;

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

public class ExpressionBuilder {

    private static List<String> functions;

    static {
        functions = new ArrayList<>();
        functions.add("environment");
        functions.add("random");
        functions.add("evaluate");
        functions.add("percent");
        functions.add("ticks");
    }
    public static Expression<Double> buildDoubleExpression(String expression, EntityDefinition entityDefinition, EnvVariablesManager env) throws BadFunctionExpressionException, MissingPropertyExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        Expression<Double> funcExpression = buildFunctionExpression(expression, entityDefinition, env);
        if (funcExpression != null) return funcExpression;
        Expression<Double> entPropertyExpression = buildEntityDoublePropertyExpression(expression, entityDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        Expression<Double> simpleExpression = buildSimpleDoubleExpression(expression);
        if (simpleExpression != null) return simpleExpression;
        throw new BadExpressionException(expression);
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

    private static Expression<Double> buildEntityDoublePropertyExpression(String expression, EntityDefinition entityDefinition) throws BadPropertyTypeExpressionException {
        Optional<String> propDef = entityDefinition.getProps().stream()
                .map(PropertyDefinition::getName)
                .filter(name -> name.equals(expression)).findFirst();
        if (propDef.isPresent()) {
            Optional<PropertyDefinition<?>> prop = entityDefinition.getProps().stream().filter(p -> p.getName().equals(expression)).findFirst();
            if (prop.isPresent() && prop.get().getType() == PropertyType.DECIMAL || prop.get().getType() == PropertyType.FLOAT)
                return new PropertyExpression<>(expression);
            else
                throw new BadPropertyTypeExpressionException(expression, prop.get().getType());
        }
        return null;
    }

    public static Expression<Double> buildFunctionExpression(String expression, EntityDefinition entityDefinition, EnvVariablesManager env) throws BadFunctionExpressionException, MissingPropertyExpressionException, BadExpressionException, BadPropertyTypeExpressionException {
        String finalExpression = expression;
        Optional<String> func = functions.stream().filter(f -> finalExpression.toLowerCase().startsWith(f)).findFirst();
        if(!func.isPresent()) return null;
        String funcName = func.get();
        expression = expression.substring(funcName.length());
        if (!expression.startsWith("(") || !expression.endsWith(")")) throw new BadFunctionExpressionException(finalExpression);
        expression = expression.substring(1, expression.length()-1);
        switch (funcName)
        {
            case "environment":
                String finalExpression1 = expression;
                env.getEnvVariables().stream().map(PropertyDefinition::getName)
                        .filter(name -> name.equals(finalExpression1)).findFirst()
                        .orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, entityDefinition, true));
                return new NumericalEnviromentExpression(expression);
            case "random":
                try {
                    return new RandomExpression(Integer.parseInt(expression));
                }catch (NumberFormatException e)
                {
                    throw new BadFunctionExpressionException(finalExpression);
                }
            case "evaluate":
                String prop = expression.substring(expression.indexOf(".")+1);
                Optional<String> propDef = entityDefinition.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new PropertyExpression<>(prop);
                }
                else
                {
                    throw new MissingPropertyExpressionException(finalExpression, entityDefinition, false);
                }
            case "percent":
                return new DualMathExpression(MathOperation.PERCENT,
                        buildDoubleExpression(expression, entityDefinition, env),
                        buildDoubleExpression(expression, entityDefinition, env)
                );
            case "ticks":
                prop = expression.substring(expression.indexOf(".") + 1);
                propDef = entityDefinition.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new TicksExpression(prop);
                }
                else {
                    throw new MissingPropertyExpressionException(finalExpression, entityDefinition, false);
                }
        }
        throw new BadFunctionExpressionException(finalExpression);
    }

    public static Expression<String> buildStringExpression(String valueExpression, EntityDefinition entityDefinition, EnvVariablesManager env) throws MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, BadExpressionException {
        Expression<String> funcExpression = buildFunctionStringExpression(valueExpression, entityDefinition, env);
        if (funcExpression != null) return funcExpression;
        Expression<String> entPropertyExpression = buildEntityStringPropertyExpression(valueExpression, entityDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleStringExpression(valueExpression);
    }

    private static Expression<String> buildSimpleStringExpression(String valueExpression) {
        return new Expression<String>() {
            @Override
            public Comparable<String> evaluate(Context context) {
                return valueExpression;
            }
        };
    }

    private static Expression<String> buildEntityStringPropertyExpression(String valueExpression, EntityDefinition entityDefinition) throws BadPropertyTypeExpressionException {
        Optional<String> propDef = entityDefinition.getProps().stream()
                .map(PropertyDefinition::getName)
                .filter(name -> name.equals(valueExpression)).findFirst();
        if (propDef.isPresent()) {
            Optional<PropertyDefinition<?>> prop = entityDefinition.getProps().stream().filter(p -> p.getName().equals(valueExpression)).findFirst();
            if (prop.isPresent() && prop.get().getType() == PropertyType.STRING)
                return new PropertyExpression<>(valueExpression);
            else
                throw new BadPropertyTypeExpressionException(valueExpression, prop.get().getType());
        }
        return null;
    }

    private static Expression<String> buildFunctionStringExpression(String valueExpression, EntityDefinition entityDefinition, EnvVariablesManager env) throws MissingPropertyExpressionException, BadFunctionExpressionException {
        String finalExpression = valueExpression;
        Optional<String> func = functions.stream().filter(f -> finalExpression.toLowerCase().startsWith(f)).findFirst();
        if(!func.isPresent()) return null;
        String funcName = func.get();
        valueExpression = valueExpression.substring(funcName.length());
        if (!valueExpression.startsWith("(") || !valueExpression.endsWith(")")) throw new BadFunctionExpressionException(finalExpression);
        valueExpression = valueExpression.substring(1, valueExpression.length()-1);
        switch (funcName)
        {
            case "environment":
                String finalExpression1 = valueExpression;
                env.getEnvVariables().stream().map(PropertyDefinition::getName)
                        .filter(name -> name.equals(finalExpression1)).findFirst()
                        .orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, entityDefinition, true));
                return new StringEnviromentExpression(valueExpression);
            case "random":
            case "percent":
            case "ticks":
                throw new BadFunctionExpressionException(finalExpression);
            case "evaluate":
                String prop = valueExpression.substring(valueExpression.indexOf(".")+1);
                Optional<String> propDef = entityDefinition.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new PropertyExpression<>(prop);
                }
                else
                {
                    throw new MissingPropertyExpressionException(finalExpression, entityDefinition, false);
                }
        }
        throw new BadFunctionExpressionException(finalExpression);
    }

    public static Expression<Boolean> buildBooleanExpression(String valueExpression, EntityDefinition entityDefinition, EnvVariablesManager env) throws MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException {
        Expression<Boolean> funcExpression = buildFunctionBooleanExpression(valueExpression, entityDefinition, env);
        if (funcExpression != null) return funcExpression;
        Expression<Boolean> entPropertyExpression = buildEntityBooleanPropertyExpression(valueExpression, entityDefinition);
        if (entPropertyExpression != null) return entPropertyExpression;
        return buildSimpleBooleanExpression(valueExpression);
    }

    private static Expression<Boolean> buildSimpleBooleanExpression(String valueExpression) {
        return context -> {
            try {
                return Boolean.parseBoolean(valueExpression);
            }catch (NumberFormatException e)
            {
                throw new RuntimeException(new BadPropertyTypeExpressionException(valueExpression, PropertyType.BOOLEAN));
            }
        };
    }

    private static Expression<Boolean> buildEntityBooleanPropertyExpression(String valueExpression, EntityDefinition entityDefinition) throws BadPropertyTypeExpressionException {
        Optional<String> propDef = entityDefinition.getProps().stream()
                .map(PropertyDefinition::getName)
                .filter(name -> name.equals(valueExpression)).findFirst();
        if (propDef.isPresent()) {
            Optional<PropertyDefinition<?>> prop = entityDefinition.getProps().stream().filter(p -> p.getName().equals(valueExpression)).findFirst();
            if (prop.isPresent() && prop.get().getType() == PropertyType.BOOLEAN)
                return new PropertyExpression<>(valueExpression);
            else
                throw new BadPropertyTypeExpressionException(valueExpression, prop.get().getType());
        }
        return null;
    }

    private static Expression<Boolean> buildFunctionBooleanExpression(String valueExpression, EntityDefinition entityDefinition, EnvVariablesManager env) throws BadFunctionExpressionException, MissingPropertyExpressionException {
        String finalExpression = valueExpression;
        Optional<String> func = functions.stream().filter(f -> finalExpression.toLowerCase().startsWith(f)).findFirst();
        if(!func.isPresent()) return null;
        String funcName = func.get();
        valueExpression = valueExpression.substring(funcName.length());
        if (!valueExpression.startsWith("(") || !valueExpression.endsWith(")")) throw new BadFunctionExpressionException(finalExpression);
        valueExpression = valueExpression.substring(1, valueExpression.length()-1);
        switch (funcName)
        {
            case "environment":
                String finalExpression1 = valueExpression;
                env.getEnvVariables().stream().map(PropertyDefinition::getName)
                        .filter(name -> name.equals(finalExpression1)).findFirst()
                        .orElseThrow(() -> new MissingPropertyExpressionException(finalExpression, entityDefinition, true));
                return new BooleanEnviromentExpression(valueExpression);
            case "random":
            case "percent":
            case "ticks":
                throw new BadFunctionExpressionException(finalExpression);
            case "evaluate":
                String prop = valueExpression.substring(valueExpression.indexOf(".")+1);
                Optional<String> propDef = entityDefinition.getProps().stream()
                        .map(PropertyDefinition::getName)
                        .filter(name -> name.equals(prop)).findFirst();
                if (propDef.isPresent()) {
                    return new PropertyExpression<>(prop);
                }
                else
                {
                    throw new MissingPropertyExpressionException(finalExpression, entityDefinition, false);
                }
        }
        throw new BadFunctionExpressionException(finalExpression);
    }
}
