package dto.subdto;

import dto.DTO;

import java.util.List;
import java.util.Map;

public class SingleRunHistoryDto implements DTO {
    private final List<String> entity;
    private final List<Map<Integer, Integer>> counts;

    private final Map<Comparable<?>, Integer> propertyHist;

    public SingleRunHistoryDto(List<String> entity,
                               List<Map<Integer, Integer>> counts,
                               Map<Comparable<?>, Integer> propertyHist) {
        this.entity = entity;
        this.propertyHist = propertyHist;
        this.counts = counts;
    }

    public List<String> getEntity() {
        return entity;
    }

    public Map<Comparable<?>, Integer> getPropertyHist() {
        return propertyHist;
    }

    public List<Map<Integer, Integer>> getCounts() {
        return counts;
    }
}
