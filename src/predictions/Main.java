package predictions;

import predictions.action.impl.IncreaseAction;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.execution.context.Context;
import predictions.execution.context.ContextImpl;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.environment.impl.EnvVariableManagerImpl;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.execution.instance.entity.EntityInstance;
import predictions.execution.instance.entity.manager.EntityInstanceManager;
import predictions.execution.instance.entity.manager.EntityInstanceManagerImpl;
import predictions.execution.instance.property.PropertyInstanceImpl;
import predictions.rule.api.Activation;
import predictions.rule.api.Rule;
import predictions.rule.impl.ActivationImpl;
import predictions.rule.impl.RuleImpl;

public class Main {

    public static void main(String[] args) {

        // definition phase - happens as part of file read and validity checks
        IntegerPropertyDefinition agePropertyDefinition = new IntegerPropertyDefinition("age", ValueGeneratorFactory.createFixed(18));
        IntegerPropertyDefinition smokingInDayPropertyDefinition = new IntegerPropertyDefinition("smokingInDay", ValueGeneratorFactory.createFixed(10));

        EntityDefinition smokerEntityDefinition = new EntityDefinitionImpl("smoker", 100);
        smokerEntityDefinition.getProps().add(agePropertyDefinition);
        smokerEntityDefinition.getProps().add(smokingInDayPropertyDefinition);

        // define rules by creating instances of actions
        Activation a = new ActivationImpl(2, 1);
        Rule rule1 = new RuleImpl("rule 1", a);
        rule1.addAction(new IncreaseAction(smokerEntityDefinition, "age", "1+environment(tax-amount)-environment(tax-amount)"));
        rule1.addAction(new IncreaseAction(smokerEntityDefinition, "smokingInDay", "3"));
        //rule1.addAction(new KillAction(smokerEntityDefinition));

        EnvVariablesManager envVariablesManager = new EnvVariableManagerImpl();
        IntegerPropertyDefinition taxAmountEnvironmentVariablePropertyDefinition = new IntegerPropertyDefinition("tax-amount", ValueGeneratorFactory.createRandomInteger(10, 100));
        envVariablesManager.addEnvironmentVariable(taxAmountEnvironmentVariablePropertyDefinition);




        // execution phase - happens upon command 3

        // initialization phase

        // creating entity instance manager
        EntityInstanceManager entityInstanceManager = new EntityInstanceManagerImpl();

        // create 3 instance of the smokerEntityDefinition smoker
        for (int i = 0; i < smokerEntityDefinition.getPopulation(); i++) {
            entityInstanceManager.create(smokerEntityDefinition);
        }

        // create env variable instance
        ActiveEnvironment activeEnvironment = envVariablesManager.createActiveEnvironment();

        int valueFromUser = 54;
        activeEnvironment.addPropertyInstance(new PropertyInstanceImpl(taxAmountEnvironmentVariablePropertyDefinition, valueFromUser));

        // during a tick...

        // given an instance...
        EntityInstance entityInstance = entityInstanceManager.getInstances().get(0);
        // create a context (per instance)
        Context context = new ContextImpl(entityInstance, entityInstanceManager, activeEnvironment);
        for (int tick=0;tick<=100;tick++) {
            if (rule1.getActivation().isActive(tick)) rule1.getActionsToPerform()
                    .forEach(action ->
                            action.invoke(context));
        }
        System.out.println(entityInstance.getPropertyByName("age").getValue());


    }


}
