package predictions.definition.property.api;

public interface PropertyDefinition {
    String getName();
    PropertyType getType();
    Object generateValue();
}