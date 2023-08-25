package console.dto.presenter.subpresenter;

import console.dto.presenter.DTOPresenter;
import dto.subdto.show.world.PropertyDto;
import dto.subdto.show.world.WorldDto;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldPresenter extends DTOPresenter {
    private final WorldDto world;
    public WorldPresenter(WorldDto world) {
        this.world = world;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        String sep = IntStream.range(0,50).mapToObj(i -> "-").collect(Collectors.joining());
        String res = sep + "\n\n";
        res += "World:\n";
        res += "\tenvironment: \n";
        res += world.getEnvironment().stream()
                .map((PropertyDto prop) -> new PropertyPresenter(prop, true))
                .map(PropertyPresenter::toString)
                .collect(Collectors.joining("\n"));
        res += "\n\tentities: \n";
        res += world.getEntities().stream()
                .map(EntityPresenter::new)
                .map(EntityPresenter::toString)
                .collect(Collectors.joining("\n"));
        res += "\n\trules: \n";
        res += world.getRules().stream()
                .map(RulePresenter::new)
                .map(RulePresenter::toString)
                .collect(Collectors.joining("\n\n"));
        res += "\n\n\ttermination options: \n";
        Integer ticksTermination = world.getTicksTermination();
        Integer timeTermination = world.getTimeTermination();
        boolean userTermination = world.isUserTermination();
        res += ticksTermination == null ? "" : "\t\tin " + ticksTermination + " ticks\n";
        res += timeTermination == null ? "" : "\t\tfor " + timeTermination + " seconds\n";
        res += !userTermination ? "" : "\t\twith user termination\n";
        res += "\n" + sep + "\n";
        return res;
    }
}
