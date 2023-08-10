package predictions.rule.impl;

import predictions.action.api.Action;
import predictions.rule.api.Activation;
import predictions.rule.api.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleImpl implements Rule {

    private final String name;
    private Activation activation;
    private final List<Action> actions;

    public RuleImpl(String name, Activation activation) {
        this.name = name;
        actions = new ArrayList<>();
        this.activation = activation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Activation getActivation() {
        return activation;
    }

    @Override
    public List<Action> getActionsToPerform() {
        return actions;
    }

    @Override
    public void addAction(Action action) {
        actions.add(action);
    }
}
