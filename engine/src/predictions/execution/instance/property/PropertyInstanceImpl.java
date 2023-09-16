package predictions.execution.instance.property;

import dto.subdto.show.instance.PropertyInstanceDto;
import predictions.definition.property.api.PropertyDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PropertyInstanceImpl<T> implements PropertyInstance<T> {

    private final PropertyDefinition<T> propertyDefinition;
    private Comparable<T> value;

    private int timeModification;

    private final List<Integer> modificationHistory;

    public PropertyInstanceImpl(PropertyDefinition<T> propertyDefinition, Comparable<T> value) {
        this.propertyDefinition = propertyDefinition;
        if (propertyDefinition.isLegal(value)) {
            this.value = value;
            modificationHistory = new ArrayList<>();
            updateModificationHistory(0);
        }
        else {
            throw new IllegalArgumentException("Illegal value for property " + propertyDefinition.getName() + ", value: " + value);
        }
    }

    @Override
    public PropertyDefinition<T> getPropertyDefinition() {
        return propertyDefinition;
    }

    @Override
    public Comparable<T> getValue() {
        return value;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void updateValue(Comparable<?> val, int timeModification) {
        if (propertyDefinition.isLegal(val)) {
            this.value = (Comparable<T>) val;
            updateModificationHistory(timeModification);
        }
        else
        {
            Double from=null ,to=null;
            if (val instanceof Integer && this.value instanceof Integer) {
                from = ((Integer) this.propertyDefinition.getDto().getFrom()).doubleValue();
                to = ((Integer) this.propertyDefinition.getDto().getTo()).doubleValue();
            }
            if (val instanceof Double && this.value instanceof Double) {
                from = (Double) this.propertyDefinition.getDto().getFrom();
                to = (Double) this.propertyDefinition.getDto().getTo();
            }
            if (from!=null)
            {
                if (val instanceof Double && from > (Double) val) {
                    this.value = (Comparable<T>) from;
                    updateModificationHistory(timeModification);
                }
                if (val instanceof Double && to < (Double) val) {
                    this.value = (Comparable<T>) to;
                    updateModificationHistory(timeModification);
                }
            }
        }
    }

    private void updateModificationHistory(int timeModification) {
        this.timeModification = timeModification;
        if (modificationHistory.isEmpty() ||
                !modificationHistory.get(modificationHistory.size()-1).equals(timeModification))
            modificationHistory.add(timeModification);
    }

    @Override
    public int getTimeModification() {
        return timeModification;
    }

    @Override
    public PropertyInstanceDto getDto() {
        return new PropertyInstanceDto(propertyDefinition.getDto(), value);
    }

    @Override
    public Double getConsistency(int tick) {
        modificationHistory.add(tick);

        // Calculate the sum of time differences between consecutive changes
        long totalTimeDifference = IntStream.range(1, modificationHistory.size())
                .mapToLong(i -> modificationHistory.get(i) - modificationHistory.get(i - 1))
                .sum();

        int changeCount = modificationHistory.size() - 1;
        modificationHistory.remove(modificationHistory.size()-1);

        // Calculate the average time between changes
         // Number of changes
        if (changeCount > 0) {
            return (double) totalTimeDifference / changeCount;
        } else {
            return null; // Return null if no valid time differences found
        }
    }

}
