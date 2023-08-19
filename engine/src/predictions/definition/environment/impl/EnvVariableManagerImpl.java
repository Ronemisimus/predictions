package predictions.definition.environment.impl;

import predictions.definition.property.impl.BooleanPropertyDefinition;
import predictions.definition.property.impl.DoublePropertyDefinition;
import predictions.definition.property.impl.IntegerPropertyDefinition;
import predictions.definition.property.impl.StringPropertyDefinition;
import predictions.definition.value.generator.api.ValueGeneratorFactory;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.impl.ActiveEnvironmentImpl;
import predictions.execution.instance.property.PropertyInstance;
import predictions.generated.PRDEnvProperty;
import predictions.generated.PRDEvironment;

import java.util.*;

import static predictions.ConverterPRDEngine.getPropertyDefinitionFromPRD;

public class EnvVariableManagerImpl implements EnvVariablesManager {

    private final Map<String, PropertyDefinition<?>> propNameToPropDefinition;

    public EnvVariableManagerImpl() {
        propNameToPropDefinition = new HashMap<>();
    }

    public EnvVariableManagerImpl(PRDEvironment prdEvironment) {
        propNameToPropDefinition = new HashMap<>();
        prdEvironment.getPRDEnvProperty().forEach(def -> propNameToPropDefinition.put(def.getPRDName(), getPropertyDefinitionFromPRD(def)));
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

    @Override
    public void set(String name, Optional<Comparable<?>> value) {
        Optional<PropertyDefinition<?>> def = Optional.ofNullable(propNameToPropDefinition.get(name));
        if (value.isPresent() && def.isPresent()) {
            switch (def.get().getType()) {
                case STRING:
                    PropertyDefinition<String> stringDef = (PropertyDefinition<String>) def.get();
                    stringDef.setInit((Comparable<String>) value.get());
                    break;
                case DECIMAL:
                    PropertyDefinition<Integer> intDef = (PropertyDefinition<Integer>) def.get();
                    intDef.setInit((Comparable<Integer>) value.get());
                    break;
                case FLOAT:
                    PropertyDefinition<Double> doubleDef = (PropertyDefinition<Double>) def.get();
                    doubleDef.setInit((Comparable<Double>) value.get());
                    break;
                case BOOLEAN:
                    PropertyDefinition<Boolean> booleanDef = (PropertyDefinition<Boolean>) def.get();
                    booleanDef.setInit((Comparable<Boolean>) value.get());
                    break;
            }
        }
    }
}
