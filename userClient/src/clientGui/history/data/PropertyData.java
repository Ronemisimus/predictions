package clientGui.history.data;

import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.world.PropertyDto;

import java.util.Map;

public class PropertyData {
    private final Map<Comparable<?>, Integer> histogram;
    private final String entity;
    private final String property;
    private final Boolean number;
    private final Double average;
    private final Double consistency;

    public PropertyData(String entity, PropertyDto p, SingleRunHistoryDto run) {
        property = p.getName();
        this.entity = entity;
        number = p.getType().equalsIgnoreCase("float") || p.getType().equalsIgnoreCase("decimal");
        histogram = run.getPropertyHist();
        consistency = run.getConsistency();
        average = run.getAverage();
    }

    public Map<Comparable<?>, Integer> getHistogram() {
        return histogram;
    }

    public String getEntity() {
        return entity;
    }

    public String getProperty() {
        return property;
    }

    public Boolean getNumber() {
        return number;
    }

    public Double getAverage() {
        return average;
    }

    public Double getConsistency() {
        return consistency;
    }
}
