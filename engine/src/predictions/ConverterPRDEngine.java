package predictions;

import predictions.action.api.Action;
import predictions.action.impl.*;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.impl.BooleanPropertyDefinition;
import predictions.definition.property.impl.DoublePropertyDefinition;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.property.impl.StringPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.generated.*;
import predictions.rule.api.Activation;
import predictions.rule.api.Rule;
import predictions.rule.impl.ActivationImpl;
import predictions.rule.impl.RuleImpl;

import java.util.ArrayList;
import java.util.List;

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

    public static Action getActionFromPRD(PRDAction def, EntityDefinition ent) {
        Action res = null;
        switch (def.getType().toLowerCase())
        {
            case "increase":
                res = new IncreaseAction(ent, def.getProperty(), def.getBy());
                break;
            case "decrease":
                res = new DecreaseAction(ent, def.getProperty(), def.getBy());
                break;
            case "kill":
                res = new KillAction(ent);
                break;
            case "set":
                res = new SetAction(ent, def.getProperty(), def.getValue());
                break;
            case "condition":
                res = new ConditionAction(ent, def.getPRDCondition(), def.getPRDThen(), def.getPRDElse());
        }
        return res;
    }

    public static Rule getRuleFromPRD(PRDRule def, PRDEntities entities) {
        Activation act = new ActivationImpl(def.getPRDActivation());
        List<Action> res = new ArrayList<>();
        for (PRDAction prdAction: def.getPRDActions().getPRDAction())
        {
            PRDEntity mainEntity = entities.getPRDEntity().stream().filter(prdEntity -> prdAction.getEntity().equals(prdEntity.getName())).findFirst().get();
            res.add(getActionFromPRD(prdAction, new EntityDefinitionImpl(mainEntity)));
        }
        Rule r = new RuleImpl(def.getName(), act);
        res.forEach(r::addAction);
        return r;
    }
}
