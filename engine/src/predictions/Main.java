package predictions;

import predictions.action.api.Action;
import predictions.action.impl.ConditionAction;
import predictions.action.impl.IncreaseAction;
import predictions.action.impl.KillAction;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.definition.world.api.World;
import predictions.definition.world.impl.WorldImpl;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.environment.impl.EnvVariableManagerImpl;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.execution.instance.world.WorldInstance;
import predictions.execution.instance.world.WorldInstanceImpl;
import predictions.expression.api.BooleanOperation;
import predictions.expression.api.Expression;
import predictions.expression.api.SingleBooleanOperation;
import predictions.expression.impl.DoubleComplexExpression;
import predictions.expression.impl.DualBooleanExpression;
import predictions.expression.impl.SingleBooleanExpression;
import predictions.rule.api.Rule;
import predictions.rule.impl.ActivationImpl;
import predictions.rule.impl.RuleImpl;
import predictions.termination.api.Termination;
import predictions.termination.impl.TicksTermination;
import predictions.termination.impl.TimeTermination;

import java.time.Duration;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        EnvVariablesManager env = new EnvVariableManagerImpl();
        env.addEnvironmentVariable(
                new IntegerPropertyDefinition(
                        "cigarets-critical",
                        ValueGeneratorFactory.createRandomInteger(10,100),10, 100));
        env.addEnvironmentVariable(
                new IntegerPropertyDefinition(
                        "cigarets-increase-non-smoker",
                        ValueGeneratorFactory.createRandomInteger(0,10), 0, 10));
        env.addEnvironmentVariable(
                new IntegerPropertyDefinition(
                        "cigarets-increase-already-smoker",
                        ValueGeneratorFactory.createRandomInteger(10,100), 10, 100));
        Set<EntityDefinition> entityDefinitions = new HashSet<>();

        EntityDefinition smoker = new EntityDefinitionImpl("Smoker", 100);

        smoker.getProps().add(
                new IntegerPropertyDefinition(
                        "lung-cancer-progress",
                        ValueGeneratorFactory.createFixed(0),0,100));
        smoker.getProps().add(
                new IntegerPropertyDefinition(
                        "age",
                        ValueGeneratorFactory.createRandomInteger(15,50),15,50));
        smoker.getProps().add(
            new IntegerPropertyDefinition(
                    "cigarets-per-month",
                    ValueGeneratorFactory.createRandomInteger(0,500),0,500));
        entityDefinitions.add(smoker);

        Set<Rule> rules = new LinkedHashSet<>();

        Rule r1 = new RuleImpl("aging", new ActivationImpl(12, 1));
        r1.addAction(new IncreaseAction(
            smoker, "age", "1"));
        rules.add(r1);

        Rule r2 = new RuleImpl("got cancer", new ActivationImpl(1, 1));
        Expression<Boolean> cond_exp = new DualBooleanExpression(
                BooleanOperation.AND,
                new SingleBooleanExpression(
                        "cigarets-per-month",
                        SingleBooleanOperation.BIGGER,
                        new DoubleComplexExpression("environment(cigarets-critical)")),
                new SingleBooleanExpression(
                        "age",
                        SingleBooleanOperation.BIGGER,
                        new DoubleComplexExpression("40")
                )
        );
        List<Action> then_actions = new ArrayList<>();
        then_actions.add(new IncreaseAction(smoker, "lung-cancer-progress", "random(5)"));
        List<Action> else_actions = new ArrayList<>();
        Action cond = new ConditionAction(smoker, cond_exp, then_actions, else_actions);
        r2.addAction(cond);
        rules.add(r2);

        Rule r3 = new RuleImpl("more-cigartes", new ActivationImpl(1, 0.3));
        Expression<Boolean> cond_exp2 = new SingleBooleanExpression(
                "cigarets-per-month",
                SingleBooleanOperation.EQUAL,
                new DoubleComplexExpression("0")
        );
        then_actions = new ArrayList<>();
        then_actions.add(new IncreaseAction(smoker, "cigarets-per-month", "environment(cigarets-increase-non-smoker)"));

        else_actions = new ArrayList<>();
        else_actions.add(new IncreaseAction(smoker, "cigarets-per-month", "environment(cigarets-increase-already-smoker)"));

        Action cond2 = new ConditionAction(smoker, cond_exp2, then_actions, else_actions);
        r3.addAction(cond2);
        rules.add(r3);

        Rule r4 = new RuleImpl("death", new ActivationImpl(1,1));
        Expression<Boolean> cond_exp3 = new SingleBooleanExpression(
            "lung-cancer-progress",
                SingleBooleanOperation.BIGGER,
            new DoubleComplexExpression("90")
        );

        then_actions = new ArrayList<>();
        then_actions.add(new KillAction(smoker));

        else_actions = new ArrayList<>();

        Action cond3 = new ConditionAction(smoker, cond_exp3, then_actions, else_actions);
        r4.addAction(cond3);
        rules.add(r4);

        Set<Termination> term = new HashSet<>();
        term.add(new TicksTermination(840));
        term.add(new TimeTermination(Duration.ofSeconds(10000)));

        World w = new WorldImpl(env, entityDefinitions, rules, term);

        WorldInstance wi = new WorldInstanceImpl(w);

        Map.Entry<Integer,Termination> res = wi.run();

        System.out.println(wi.getEntityCounts());


    }


}
