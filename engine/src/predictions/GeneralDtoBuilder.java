package predictions;

import dto.subdto.SingleRunHistoryDto;
import predictions.execution.EntityCountHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralDtoBuilder {
    public static SingleRunHistoryDto buildSingleRunDtoEntity(Map<String, EntityCountHistory> res) {
        List<String> entities = new ArrayList<>(res.keySet());
        List<Integer> startCount = entities.stream().map(name->(EntityCountHistory)res.get(name))
                .map(EntityCountHistory::getInitialCount).collect(Collectors.toList());
        List<Integer> finalCount = entities.stream().map(name->(EntityCountHistory)res.get(name))
                .map(EntityCountHistory::getEndCount).collect(Collectors.toList());
        return new SingleRunHistoryDto(entities, startCount,finalCount, null);
    }

    public static SingleRunHistoryDto buildSingleRunDtoProperty(Map<Comparable<?>, Integer> propertyHist) {
        return new SingleRunHistoryDto(null,null,null,propertyHist);
    }
}
