package console.dto.presenter;

import dto.subdto.SingleRunHistoryDto;
import dto.subdto.show.world.EntityDto;
import dto.subdto.show.world.PropertyDto;

import java.util.stream.Collectors;

public class RunHistoryPresenter extends DTOPresenter{

    private final SingleRunHistoryDto dto;
    private final EntityDto ent;
    private final PropertyDto prop;
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

        StringBuilder entityHistogram = new StringBuilder();

        if (ent==null)
        {
            for (int i=0;i<dto.getEntity().size();i++)
            {
                entityHistogram.append("\t").append(dto.getEntity().get(i)).append(" appeared at the start: ").append(dto.getStartCount().get(i)).append(" times and at the end: ").append(dto.getEndCount().get(i)).append(" times\n");
            }
        }


        return "Run history: (" + ((ent==null)?"entity histogram":"property histogram")+")\n" +
                (ent!=null?
                "for entity named: " + ent.getName() +
                        " with property named: " + prop.getName() +
                        " of type: " + prop.getType() + "\n": "") +
                (ent==null?
                        entityHistogram.toString() :
                        "\t" + dto.getPropertyHist().entrySet().stream()
                                .map(e -> "value " + e.getKey() + " appeared " + e.getValue() + " times")
                                .collect(Collectors.joining("\n\t")) +
                                "\n\nvalue distribution at end of simulation"+
                                "\n(if nothing appears above there are no live entities of "+ent.getName()+")\n\n") ;
    }
}
