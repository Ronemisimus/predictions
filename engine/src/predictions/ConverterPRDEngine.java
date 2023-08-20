package predictions;

import predictions.action.api.Action;
import predictions.action.impl.*;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.impl.BooleanPropertyDefinition;
import predictions.definition.property.impl.DoublePropertyDefinition;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.property.impl.StringPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.exception.*;
import predictions.expression.api.MathOperation;
import predictions.generated.*;
import predictions.rule.api.Activation;
import predictions.rule.api.Rule;
import predictions.rule.impl.ActivationImpl;
import predictions.rule.impl.RuleImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ConverterPRDEngine {
    private ConverterPRDEngine() {}

    public static PropertyDefinition<?> getPropertyDefinitionFromPRD(PRDEnvProperty def) {
        PropertyDefinition<?> res = null;
        switch (def.getType().toLowerCase())
        {
            case "decimal":
                Double from = def.getPRDRange().getFrom();
                Double to = def.getPRDRange().getTo();
                Comparable<Integer> value = (from != null) ? (int) from.doubleValue() : ((to != null) ? (int) to.doubleValue() : 0);
                res = new IntegerPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(value), (int) def.getPRDRange().getFrom(), (int) def.getPRDRange().getTo());
                break;
            case "float":
                from = def.getPRDRange().getFrom();
                to = def.getPRDRange().getTo();
                Comparable<Double> doubleValue = (from != null) ? from : ((to != null) ?  to : 0.);
                res = new DoublePropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(doubleValue), def.getPRDRange().getFrom(), def.getPRDRange().getTo());
                break;
            case "string":
                res = new StringPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(""));
                break;
            case "boolean":
                res = new BooleanPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(false));
                break;
        }
        return res;
    }

    public static PropertyDefinition<?> getPropertyDefinitionFromPRD(PRDProperty def) {
        PropertyDefinition<?> res = null;
        switch (def.getType().toLowerCase())
        {
            case "decimal":
                Double from = def.getPRDRange().getFrom();
                Double to = def.getPRDRange().getTo();
                Comparable<Integer> value = (from != null) ? (int) from.doubleValue() : ((to != null) ? (int) to.doubleValue() : 0);
                res = new IntegerPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(value), (int) def.getPRDRange().getFrom(), (int) def.getPRDRange().getTo());
                break;
            case "float":
                from = def.getPRDRange().getFrom();
                to = def.getPRDRange().getTo();
                Comparable<Double> doubleValue = (from != null) ? from : ((to != null) ?  to : 0.);
                res = new DoublePropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(doubleValue), def.getPRDRange().getFrom(), def.getPRDRange().getTo());
                break;
            case "string":
                res = new StringPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(""));
                break;
            case "boolean":
                res = new BooleanPropertyDefinition(def.getPRDName(), ValueGeneratorFactory.createFixed(false));
                break;
        }
        return res;
    }

    public static Action getActionFromPRD(PRDAction def, EntityDefinition ent, EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, MissingPropertyActionException {
        Action res = null;
        switch (def.getType().toLowerCase())
        {
            case "increase":
                res = new IncreaseAction(ent, def.getProperty(), def.getBy(), env);
                break;
            case "decrease":
                res = new DecreaseAction(ent, def.getProperty(), def.getBy(), env);
                break;
            case "kill":
                res = new KillAction(ent);
                break;
            case "set":
                res = new SetAction(ent, def.getProperty(), def.getValue(), env);
                break;
            case "condition":
                res = new ConditionAction(ent, def.getPRDCondition(), def.getPRDThen(), def.getPRDElse(), env);
                break;
            case "calculation":
                MathOperation ops[] = ConverterPRDEngine.getCalculationOps(def.getPRDMultiply(), def.getPRDDivide());
                String args1[] = ConverterPRDEngine.getArgs1(def.getPRDMultiply(), def.getPRDDivide());
                String args2[] = ConverterPRDEngine.getArgs2(def.getPRDMultiply(), def.getPRDDivide());
                res = new CalculationAction(ent, def.getResultProp(), ops, args1, args2, env);
                break;
        }
        return res;
    }

    private static String[] getArgs2(PRDMultiply prdMultiply, PRDDivide prdDivide) {
        String[] res = new String[
                (prdMultiply != null ? 1 : 0) + (prdDivide != null ? 1 : 0)];
        int clear = 0;
        if (prdMultiply != null)
        {
            res[clear++] = prdMultiply.getArg2();
        }
        if (prdDivide != null)
        {
            res[clear++] = prdDivide.getArg2();
        }
        return res;
    }

    private static String[] getArgs1(PRDMultiply prdMultiply, PRDDivide prdDivide) {
        String[] res = new String[
                (prdMultiply != null ? 1 : 0) + (prdDivide != null ? 1 : 0)];
        int clear = 0;
        if (prdMultiply != null)
        {
            res[clear++] = prdMultiply.getArg1();
        }
        if (prdDivide != null)
        {
            res[clear++] = prdDivide.getArg1();
        }
        return res;
    }

    private static MathOperation[] getCalculationOps(PRDMultiply prdMultiply, PRDDivide prdDivide) {
        MathOperation[] res = new MathOperation[
                ((prdMultiply != null) ? 1 : 0) + ((prdDivide != null) ? 1 : 0)
                ];
        int clear = 0;
        if (prdMultiply != null)
        {
            res[clear++] = MathOperation.MULTIPLY;
        }
        if (prdDivide != null)
        {
            res[clear++] = MathOperation.DIVIDE;
        }
        return res;
    }

    public static Rule getRuleFromPRD(PRDRule def, PRDEntities entities, EnvVariablesManager env) throws BadExpressionException, MissingPropertyExpressionException, BadFunctionExpressionException, BadPropertyTypeExpressionException, MissingPropertyActionException, NoSuchEntityActionException {
        Activation act = new ActivationImpl(def.getPRDActivation());
        List<Action> res = new ArrayList<>();
        for (PRDAction prdAction: def.getPRDActions().getPRDAction()) {
            try {
                PRDEntity mainEntity = entities.getPRDEntity().stream().filter(prdEntity -> prdAction.getEntity().equals(prdEntity.getName())).findFirst().get();
                res.add(getActionFromPRD(prdAction, new EntityDefinitionImpl(mainEntity), env));
            } catch (NoSuchElementException e){
                throw new NoSuchEntityActionException(prdAction.getEntity());
            }
        }
        Rule r = new RuleImpl(def.getName(), act);
        res.forEach(r::addAction);
        return r;
    }
}
