package dto.subdto;

import dto.DTO;
import dto.subdto.show.world.EntityDto;

import java.util.List;
import java.util.Map;

public class SingleRunHistoryDto implements DTO {
    private final List<String> entity;

    private final List<Integer> startCount;
    private final List<Integer> endCount;

    private final Map<Comparable<?>, Integer> propertyHist;

    public SingleRunHistoryDto(List<String> entity,
                               List<Integer> startCount,
                               List<Integer> endCount,
                               Map<Comparable<?>, Integer> propertyHist) {
        this.entity = entity;
        this.endCount = endCount;
        this.propertyHist = propertyHist;
        this.startCount =startCount;
    }

    public List<String> getEntity() {
        return entity;
    }

    public List<Integer> getEndCount() {
        return endCount;
    }

    public Map<Comparable<?>, Integer> getPropertyHist() {
        return propertyHist;
    }

    public List<Integer> getStartCount() {
        return startCount;
    }
}
