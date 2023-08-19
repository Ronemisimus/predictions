package console.dto.presenter;

import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunHistoryPresenter extends DTOPresenter{

    private SingleRunHistoryDto dto;
    private EntityDto ent;
    private PropertyDto prop;
    public RunHistoryPresenter(SingleRunHistoryDto res) {
        this(res,null,null);
    }

    public RunHistoryPresenter(SingleRunHistoryDto res, EntityDto ent, PropertyDto prop) {
        this.dto= res;
        this.ent = ent;
        this.prop = prop;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {

        String entityHistogram = "";

        if (ent==null)
        {
            for (int i=0;i<dto.getEntity().size();i++)
            {
                entityHistogram += "\t" + dto.getEntity().get(i) + " appeared at the start: " +
                        dto.getStartCount().get(i) + " times and at the end: " + dto.getEndCount().get(i) + " times\n";
            }
        }


        return "Run history: (" + ((ent==null)?"entity histogram":"property histogram")+")\n" +
                (ent!=null?
                "for entity named: " + ent.getName() +
                        " with property named: " + prop.getName() +
                        " of type: " + prop.getType() + "\n": "") +
                (ent==null?
                        entityHistogram :
                        "\t" + dto.getPropertyHist().entrySet().stream()
                                .map(e -> "value " + e.getKey() + " appeared " + e.getValue() + " times")
                                .collect(Collectors.joining("\n\t"))) + "\n\nvalue distribution at end of simulation\n";
    }
}
