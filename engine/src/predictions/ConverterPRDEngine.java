package predictions;

import dto.ReadFileDto;
import dto.subdto.read.dto.EntityErrorDto;
import dto.subdto.read.dto.EnvironmentErrorDto;
import dto.subdto.read.dto.PropertyBadDto;
import dto.subdto.read.dto.rule.ActionErrorDto;
import dto.subdto.read.dto.rule.RuleErrorDto;
import predictions.action.api.Action;
import predictions.action.api.ContextDefinition;
import predictions.action.impl.*;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.definition.property.impl.BooleanPropertyDefinition;
import predictions.definition.property.impl.DoublePropertyDefinition;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.property.impl.StringPropertyDefinition;
import predictions.definition.value.generator.api.ValueGenerator;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.expression.api.MathOperation;
import predictions.generated.*;
import predictions.rule.api.Activation;
import predictions.rule.api.Rule;
import predictions.rule.impl.ActivationImpl;
import predictions.rule.impl.RuleImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConverterPRDEngine {
    private ConverterPRDEngine() {}

    public static PropertyDefinition<?> getPropertyDefinitionFromPRD(PRDEnvProperty def, Comparable<?> val, ReadFileDto.Builder builder) {
        return getPropertyDefinitionFromPRDinner(def.getType(), def.getPRDRange(), def.getPRDName(), val, false, true, builder);
    }

    private static PropertyDefinition<?> getPropertyDefinitionFromPRDinner(String propertyType,
                                                                           PRDRange range,
                                                                           String propertyName,
                                                                           Comparable<?> value,
                                                                           Boolean random,
                                                                           Boolean environmentProp,
                                                                           ReadFileDto.Builder builder) {
        PropertyDefinition<?> res = null;
        Double from=null, to=null;
        Comparable<?> defaultValue = null;

        propertyValidation(propertyName, random, range, propertyType, value, environmentProp,builder);

        if (range!=null) {
            from = range.getFrom();
            to = range.getTo();
        }

        switch (propertyType.toLowerCase())
        {
            case "decimal":
                defaultValue = from == null? 0 : from.intValue();
                ValueGenerator<Integer> vg = value==null? (random && from!=null? ValueGeneratorFactory.createRandomInteger(from.intValue(),to.intValue()): ValueGeneratorFactory.createFixed((Integer)defaultValue)):
                        ValueGeneratorFactory.createFixed((Integer)value);
                res = new IntegerPropertyDefinition(propertyName,
                        vg,
                        from != null ? from.intValue() : null,
                        to != null ? to.intValue() : null);
                break;
            case "float":
                defaultValue = from == null? 0. : from.doubleValue();
                ValueGenerator<Double> vgd = value==null? (random?ValueGeneratorFactory.createRandomDouble(from,to): ValueGeneratorFactory.createFixed((Double)defaultValue)):
                        ValueGeneratorFactory.createFixed((Double)value);
                res = new DoublePropertyDefinition(propertyName,
                        vgd,
                        from,
                        to);
                break;
            case "string":
                res = new StringPropertyDefinition(propertyName,
                        ValueGeneratorFactory.createFixed(value==null?"":value.toString()));
                break;
            case "boolean":
                ValueGenerator<Boolean> vgb = value==null? (random?ValueGeneratorFactory.createRandomBoolean():ValueGeneratorFactory.createFixed(false)):
                        ValueGeneratorFactory.createFixed((Boolean)value);
                res = new BooleanPropertyDefinition(propertyName,
                        vgb);
                break;
        }
        return res;
    }

    private static void propertyValidation(String propertyName,
                                           Boolean random,
                                           PRDRange range,
                                           String propertyType,
                                           Comparable<?> value,
                                           Boolean environmentProp,
                                           ReadFileDto.Builder builder) {
        PropertyBadDto.Builder res = new PropertyBadDto.Builder(propertyName);
        try {
            if (random && value != null) {
                res.environmentError(environmentProp)
                        .message("Bad property: " + propertyName + "cannot be random when value is given: " + value)
                        .valueGivenOnRandom(value);
                throw new RuntimeException("Bad property: " + propertyName + "cannot be random when value is given: " + value);
            }
            if (range != null) {
                Double from = range.getFrom();
                Double to = range.getTo();
                checkRange(propertyType,
                        from,
                        to,
                        environmentProp,
                        res);
            } else {
                if (random && !propertyType.equalsIgnoreCase("boolean")) {
                    res.environmentError(environmentProp).
                            message("Bad property: " + propertyName + "cannot be random without a range unless it is boolean").
                            badRandom();
                    throw new RuntimeException("Bad property: " + propertyName + "cannot be random without a range unless it is boolean");
                }
            }
            if (value != null) {
                if (!propertyType.equalsIgnoreCase(getValueType(value))) {
                    res.environmentError(environmentProp)
                            .message("Bad value: " + value + " for property " + propertyName)
                            .badValueType(value, propertyType);
                    throw new RuntimeException("Bad value: " + value + " for property " + propertyName);
                }
                if (range != null) {
                    double from = range.getFrom();
                    double to = range.getTo();
                    double valueD = (value instanceof Integer) ? ((Integer) value).doubleValue() : (Double) value;
                    if (valueD < from || valueD > to) {
                        res.environmentError(environmentProp)
                                .message("Bad range")
                                .valueOutOfRange(value, from, to).build();
                        throw new RuntimeException("Bad range");
                    }
                }
            }
        }catch (Exception e){
            setPropertyBadinBuilder(builder, res, environmentProp);
        }
    }

    private static void setPropertyBadinBuilder(ReadFileDto.Builder builder,
                                                PropertyBadDto.Builder res,
                                                Boolean environmentProp) {
        if (environmentProp)
        {
            builder.environmentError(
                    new EnvironmentErrorDto.Builder()
                            .envPropertyError(res.build()).build());
        }
        else
        {
            builder.entityError(
                    new EntityErrorDto.Builder()
                            .propertyError(res.build()).build()
            );
        }
        res.throwIfError();
    }

    private static void checkRange(String propertyType,
                                   Double from,
                                   Double to,
                                   Boolean environmentProp,
                                   PropertyBadDto.Builder builder) {
        switch (propertyType.toLowerCase()) {
            case "decimal":
            case "float":
                if (from > to) {
                    builder.environmentError(environmentProp)
                            .message("flipped range")
                            .rangeFlipped(from, to);
                    throw new RuntimeException("flipped range");
                }
                break;
            case "string":
            case "boolean":
                builder.environmentError(environmentProp)
                        .message("bad range type")
                        .badRangedType(true, propertyType);
                throw new RuntimeException("bad range type");
            default:
        }
    }

    private static String getValueType(Comparable<?> value) {
        if (value instanceof Integer)
            return "decimal";
        else if (value instanceof Double)
            return "float";
        else if (value instanceof String)
            return "string";
        else if (value instanceof Boolean)
            return "boolean";
        return  null;
    }

    public static PropertyDefinition<?> getPropertyDefinitionFromPRDEntity(PRDProperty def, ReadFileDto.Builder builder) {
        Comparable<?> res = null;
        boolean random = false;
        random = def.getPRDValue()!=null && def.getPRDValue().isRandomInitialize();
        if (def.getPRDValue()!=null && def.getPRDValue().getInit()!=null) {
            try {
                switch (def.getType().toLowerCase()) {
                    case "decimal":
                        res = Integer.parseInt(def.getPRDValue().getInit());
                        break;
                    case "float":
                        res = Double.parseDouble(def.getPRDValue().getInit());
                        break;
                    case "string":
                        res = def.getPRDValue().getInit();
                        break;
                    case "boolean":
                        res = Boolean.getBoolean(def.getPRDValue().getInit());
                        break;
                }
            }catch (Exception e) {
                throw new RuntimeException("Invalid init value" + def.getPRDValue().getInit() + " for property " + def.getPRDName());
            }
        }
        return getPropertyDefinitionFromPRDinner(def.getType(), def.getPRDRange(), def.getPRDName(), res, random, false, builder);
    }

    public static Action getActionFromPRD(PRDAction def,
                                              ContextDefinition contextDefinition,
                                          ActionErrorDto.Builder actionBuilder) {
        Action res = null;
        switch (def.getType().toLowerCase())
        {
            case "increase":
                res = new IncreaseAction(contextDefinition,
                        def.getEntity(),
                        def.getProperty(),
                        def.getBy(),
                        actionBuilder);
                break;
            case "decrease":
                res = new DecreaseAction(contextDefinition,
                        def.getEntity(),
                        def.getProperty(),
                        def.getBy(),
                        actionBuilder);
                break;
            case "kill":
                res = new KillAction(contextDefinition,
                        def.getEntity(),
                        actionBuilder);
                break;
            case "set":
                res = new SetAction(contextDefinition,
                        def.getEntity(),
                        def.getProperty(),
                        def.getValue(),
                        actionBuilder);
                break;
            case "condition":
                res = new ConditionAction(contextDefinition, def.getPRDCondition(), def.getPRDThen(), def.getPRDElse(), actionBuilder);
                break;
            case "calculation":
                MathOperation[] ops = ConverterPRDEngine.getCalculationOps(def.getPRDMultiply(), def.getPRDDivide());
                String[] args1 = ConverterPRDEngine.getArgs1(def.getPRDMultiply(), def.getPRDDivide());
                String[] args2 = ConverterPRDEngine.getArgs2(def.getPRDMultiply(), def.getPRDDivide());
                res = new CalculationAction(contextDefinition,
                        def.getEntity(),
                        def.getResultProp(),
                        ops,
                        args1,
                        args2,
                        actionBuilder);
                break;
            case "proximity":
                res = new ProximityAction(contextDefinition,
                        def.getPRDEnvDepth().getOf(),
                        def.getPRDActions(),
                        actionBuilder);
                break;
            case "replace":
                res = new ReplaceAction(contextDefinition,
                        def.getKill(),
                        def.getCreate(),
                        def.getMode(),
                        actionBuilder);
                break;
        }
        return res;
    }

    public static Optional<PropertyDefinition<?>> checkEntityAndPropertyInContext(String entityName,
                                                       String property,
                                                       ContextDefinition contextDefinition,
                                                       ActionErrorDto.Builder builder)
    {
        if (!contextDefinition.getPrimaryEntityDefinition().getName().equals(entityName) &&
                ( contextDefinition.getSecondaryEntityDefinition() == null ||
                !contextDefinition.getSecondaryEntityDefinition().getName().equals(entityName)))
        {
            builder.entityNotInContext(entityName);
            throw new RuntimeException("bad entity name");
        }
        Optional<PropertyDefinition<?>> prop;
        if (contextDefinition.getPrimaryEntityDefinition().getName().equals(entityName))
        {
            prop = contextDefinition.getPrimaryEntityDefinition().getProps().stream()
                    .filter(p -> p.getName().equals(property)).findFirst();
        }
        else
        {
            prop = contextDefinition.getSecondaryEntityDefinition().getProps().stream()
                    .filter(p -> p.getName().equals(property)).findFirst();
        }
        if (!prop.isPresent()) {
            builder.propertyNotInContext(property);
            throw new RuntimeException("property not in context");
        }
        return prop;
    }

    private static String[] getArgs2(PRDMultiply prdMultiply,
                                     PRDDivide prdDivide) {
        String[] res = new String[
                (prdMultiply != null ? 1 : 0) + (prdDivide != null ? 1 : 0)];
        int clear = 0;
        if (prdMultiply != null)
        {
            res[clear++] = prdMultiply.getArg2();
        }
        if (prdDivide != null)
        {
            res[clear] = prdDivide.getArg2();
        }
        return res;
    }

    private static String[] getArgs1(PRDMultiply prdMultiply,
                                     PRDDivide prdDivide) {
        String[] res = new String[
                (prdMultiply != null ? 1 : 0) + (prdDivide != null ? 1 : 0)];
        int clear = 0;
        if (prdMultiply != null)
        {
            res[clear++] = prdMultiply.getArg1();
        }
        if (prdDivide != null)
        {
            res[clear] = prdDivide.getArg1();
        }
        return res;
    }

    private static MathOperation[] getCalculationOps(PRDMultiply prdMultiply,
                                                     PRDDivide prdDivide) {
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
            res[clear] = MathOperation.DIVIDE;
        }
        return res;
    }

    public static Rule getRuleFromPRD(PRDRule def,
                                      PRDEntities entities,
                                      EnvVariablesManager env,
                                      ReadFileDto.Builder builder) {
        RuleErrorDto.Builder buildRule = new RuleErrorDto.Builder();
        try {
            Activation act = new ActivationImpl(def.getPRDActivation(), buildRule);

            List<Action> res = new ArrayList<>();
            for (PRDAction prdAction: def.getPRDActions().getPRDAction()) {
                String primaryEntityName = getPrimaryFromPRD(prdAction);
                String secondaryEntityName = getSecondaryFromPRD(prdAction);
                Optional<PRDEntity> mainEntityOpt = entities.getPRDEntity().stream()
                        .filter(prdEntity -> prdEntity.getName().equals(primaryEntityName)).findFirst();
                Optional<PRDEntity> secondaryEntityOpt = entities.getPRDEntity().stream()
                        .filter(prdEntity -> prdEntity.getName().equals(secondaryEntityName)).findFirst();
                Optional<String> secondaryEntity = prdAction.getPRDSecondaryEntity() == null ?
                        Optional.empty() :
                        prdAction.getPRDSecondaryEntity().getPRDSelection() == null ?
                                Optional.empty() :
                                Optional.of(prdAction.getPRDSecondaryEntity().getPRDSelection().getCount());
                Optional<PRDCondition> prdCondition = prdAction.getPRDSecondaryEntity() == null ?
                        Optional.empty() :
                        prdAction.getPRDSecondaryEntity().getPRDSelection() == null ?
                                Optional.empty() :
                                Optional.of(prdAction.getPRDSecondaryEntity().getPRDSelection().getPRDCondition());
                ActionErrorDto.Builder builderAction = new ActionErrorDto.Builder();
                builderAction.actionType(prdAction.getType().toLowerCase());
                try {
                    if ((primaryEntityName!=null && !mainEntityOpt.isPresent()) ||
                            (secondaryEntityName!=null && !secondaryEntityOpt.isPresent()))
                    {
                        if (primaryEntityName!=null)
                            builderAction.noEntityNamed(primaryEntityName);
                        if (secondaryEntityName!=null)
                            builderAction.noEntityNamed(secondaryEntityName);
                        throw new RuntimeException("bad entity name");
                    }
                    ContextDefinition context = ContextDefinitionImpl.getInstance(
                            mainEntityOpt.orElse(null),
                            secondaryEntityOpt.orElse(null),
                            secondaryEntity.isPresent() ? secondaryEntity.get() : "all",
                            prdCondition.orElse(null),
                            env,
                            entities.getPRDEntity() == null ? new ArrayList<>() :
                                    entities.getPRDEntity().stream().map(prdEntity -> new EntityDefinitionImpl(prdEntity, builder)).collect(Collectors.toList()),
                            primaryEntityName,
                            buildRule,
                            builder,
                            builderAction
                    );
                    res.add(getActionFromPRD(prdAction, context, builderAction));
                } catch (Exception e) {
                    buildRule.actionError(builderAction.build());
                    throw e;
                }
            }
            Rule r = new RuleImpl(def.getName(), act);
            res.forEach(r::addAction);
            return r;
        }catch(Exception e){
            builder.ruleError(buildRule.build());
            throw e;
        }
    }

    private static String getSecondaryFromPRD(PRDAction prdAction) {
        return prdAction.getPRDSecondaryEntity() == null?
                (prdAction.getPRDBetween() == null?
                null: prdAction.getPRDBetween().getTargetEntity()):
                prdAction.getPRDSecondaryEntity().getEntity();
    }

    private static String getPrimaryFromPRD(PRDAction prdAction) {
        return prdAction.getEntity() == null?
                (prdAction.getPRDBetween() == null?
                null: prdAction.getPRDBetween().getSourceEntity()):
                prdAction.getEntity();
    }
}
