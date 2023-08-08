package predictions;

import predictions.action.impl.IncreaseAction;
import predictions.action.impl.KillAction;
import predictions.definition.entity.EntityDefinition;
import predictions.definition.entity.EntityDefinitionImpl;
import predictions.execution.context.Context;
import predictions.execution.context.ContextImpl;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.environment.impl.EnvVariableManagerImpl;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.execution.instance.enitty.EntityInstance;
import predictions.execution.instance.enitty.manager.EntityInstanceManager;
import predictions.execution.instance.enitty.manager.EntityInstanceManagerImpl;
import predictions.execution.instance.property.PropertyInstanceImpl;
import predictions.rule.Rule;
import predictions.rule.RuleImpl;

public class Main {

    public static void main(String[] args) {

        // definition phase - happens as part of file read and validity checks
        IntegerPropertyDefinition agePropertyDefinition = new IntegerPropertyDefinition("age", ValueGeneratorFactory.createRandomInteger(10, 50));
        IntegerPropertyDefinition smokingInDayPropertyDefinition = new IntegerPropertyDefinition("smokingInDay", ValueGeneratorFactory.createFixed(10));

        EntityDefinition smokerEntityDefinition = new EntityDefinitionImpl("smoker", 100);
        smokerEntityDefinition.getProps().add(agePropertyDefinition);
        smokerEntityDefinition.getProps().add(smokingInDayPropertyDefinition);

        // define rules by creating instances of actions
        Rule rule1 = new RuleImpl("rule 1");
        rule1.addAction(new IncreaseAction(smokerEntityDefinition, "age", "1"));
        rule1.addAction(new IncreaseAction(smokerEntityDefinition, "smokingInDay", "3"));
        rule1.addAction(new KillAction(smokerEntityDefinition));

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
        // all available environment variable with their definition
//        for (PropertyDefinition propertyDefinition : envVariablesManager.getEnvVariables()) {

            // collect value from user...
            int valueFromUser = 54;
            activeEnvironment.addPropertyInstance(new PropertyInstanceImpl(taxAmountEnvironmentVariablePropertyDefinition, valueFromUser));
//        }

        // all env variable not inserted by user, needs to be generated randomly. lucky we have all data needed for it...
        //Integer randomEnvVariableValue = taxAmountEnvironmentVariablePropertyDefinition.generateValue();
        //activeEnvironment.addPropertyInstance(new PropertyInstanceImpl(taxAmountEnvironmentVariablePropertyDefinition, randomEnvVariableValue));

        // during a tick...

        // given an instance...
        EntityInstance entityInstance = entityInstanceManager.getInstances().get(0);
        // create a context (per instance)
        Context context = new ContextImpl(entityInstance, entityInstanceManager, activeEnvironment);
        if (rule1.getActivation().isActive(1)) {
            rule1
                    .getActionsToPerform()
                    .forEach(action ->
                            action.invoke(context));
        }
    }


}
