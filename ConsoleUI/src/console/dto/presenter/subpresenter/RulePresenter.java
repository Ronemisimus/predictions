package console.dto.presenter.subpresenter;

import console.dto.presenter.DTOPresenter;
import dto.subdto.show.world.RuleDto;

import java.util.stream.Collectors;

public class RulePresenter extends DTOPresenter {

    private RuleDto rule;
    public RulePresenter(RuleDto rule)
    {
        this.rule = rule;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        String tab= "\t\t";
        return tab+"rule name: " + rule.getName() + "\n" +
                tab+"activation: \n" +
                tab+"\tevery " + rule.getTicks() + " ticks\n" +
                tab+"\twith probability of " + Math.round(rule.getProbability()*10000)/100 + "% \n" +
                tab+"actions to preform if activated: (total of " + rule.getActions().size() + " actions) \n" +
                tab+rule.getActions().stream()
                        .map(ActionPresenter::new)
                        .map(ActionPresenter::toString)
                        .collect(Collectors.joining("\n\n"+tab));
    }
}
