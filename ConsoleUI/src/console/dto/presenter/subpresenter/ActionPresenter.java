package console.dto.presenter.subpresenter;

import console.dto.presenter.DTOPresenter;
import dto.subdto.show.world.action.ActionDto;

public class ActionPresenter extends DTOPresenter {

    private final ActionDto action;
    public ActionPresenter(ActionDto action)
    {
        this.action = action;
    }
    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        return "\t" + action.getType();
    }
}
