package predictions.definition.environment.impl;

import dto.ReadFileDto;
import dto.subdto.read.dto.EnvironmentErrorDto;
import dto.subdto.read.dto.RepeatPropertyDto;
import predictions.definition.environment.api.EnvVariablesManager;
import predictions.definition.property.api.PropertyDefinition;
import predictions.execution.instance.environment.api.ActiveEnvironment;
import predictions.execution.instance.environment.impl.ActiveEnvironmentImpl;
import predictions.generated.PRDEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static predictions.ConverterPRDEngine.getPropertyDefinitionFromPRD;

public class EnvVariableManagerImpl implements EnvVariablesManager {

    private final Map<String, PropertyDefinition<?>> propNameToPropDefinition;

    public EnvVariableManagerImpl(PRDEnvironment prdEnvironment, ReadFileDto.Builder builder) {
        propNameToPropDefinition = new HashMap<>();
        prdEnvironment.getPRDEnvProperty().forEach(def -> {
            if (propNameToPropDefinition.getOrDefault(def.getPRDName(), null) != null)
            {
                builder.environmentError(
                        new EnvironmentErrorDto.Builder()
                                .repeatPropertyError(
                                        new RepeatPropertyDto(
                                                def.getPRDName(),
                                                true,
                                                null
                                        )
                                ).build()
                );
                throw new RuntimeException("Duplicate environment variable: " + def.getPRDName());
            }
            propNameToPropDefinition.put(def.getPRDName(), getPropertyDefinitionFromPRD(def, null, builder));
        });
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
    public void set(String name, Comparable<?> value) {
        Optional<PropertyDefinition<?>> def = Optional.ofNullable(propNameToPropDefinition.get(name));
        if (def.isPresent()) {
            if(value instanceof String) {
                //noinspection unchecked
                PropertyDefinition<String> stringDef = (PropertyDefinition<String>) def.get();
                stringDef.setInit((String)value);
            } else if(value instanceof Integer) {
                //noinspection unchecked
                PropertyDefinition<Integer> intDef = (PropertyDefinition<Integer>) def.get();
                intDef.setInit((Integer) value);
            } else if (value instanceof Double) {
                //noinspection unchecked
                PropertyDefinition<Double> doubleDef = (PropertyDefinition<Double>) def.get();
                doubleDef.setInit((Double) value);
            } else if (value instanceof Boolean) {
                //noinspection unchecked
                PropertyDefinition<Boolean> booleanDef = (PropertyDefinition<Boolean>) def.get();
                booleanDef.setInit((Boolean) value);
            }
        }
    }
}
