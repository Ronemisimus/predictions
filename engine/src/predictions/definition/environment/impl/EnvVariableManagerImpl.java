package predictions.definition.environment.impl;

import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.impl.ActiveEnvironmentImpl;
import predictions.execution.instance.property.PropertyInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvVariableManagerImpl implements EnvVariablesManager {

    private final Map<String, PropertyDefinition<?>> propNameToPropDefinition;

    public EnvVariableManagerImpl() {
        propNameToPropDefinition = new HashMap<>();
    }

    @Override
    public void addEnvironmentVariable(PropertyDefinition<?> propertyDefinition) {
        propNameToPropDefinition.put(propertyDefinition.getName(), propertyDefinition);
    }

    @Override
    public ActiveEnvironment createActiveEnvironment() {
        ActiveEnvironment res = new ActiveEnvironmentImpl();
        getEnvVariables().forEach(def -> res.addPropertyInstance(PropertyDefinition.instantiate(def)));
        return res;
    }

    @Override
    public Collection<PropertyDefinition<?>> getEnvVariables() {
        return propNameToPropDefinition.values();
    }
}
