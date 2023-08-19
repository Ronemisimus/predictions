package predictions.execution.instance.environment.impl;

import dto.subdto.InitializeDto;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.property.PropertyInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ActiveEnvironmentImpl implements ActiveEnvironment {

    private final Map<String, PropertyInstance<?>> envVariables;

    public ActiveEnvironmentImpl() {
        envVariables = new HashMap<>();
    }

    @Override
    public PropertyInstance<?> getProperty(String name) {
        if (!envVariables.containsKey(name)) {
            throw new IllegalArgumentException("Can't find env variable with name " + name);
        }
        return envVariables.get(name);
    }

    @Override
    public void addPropertyInstance(PropertyInstance<?> propertyInstance) {
        envVariables.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
    }

    @Override
    public Set<String> getEnvVariableNames() {
        return envVariables.keySet();
    }

    @Override
    public InitializeDto getDto() {
        return new InitializeDto(envVariables.values().stream()
                .map(PropertyInstance::getDto).collect(Collectors.toList()));
    }
}
