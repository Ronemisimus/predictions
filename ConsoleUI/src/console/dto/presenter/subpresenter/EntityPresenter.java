package console.dto.presenter.subpresenter;

import console.dto.presenter.DTOPresenter;
import dto.subdto.show.world.EntityDto;

import java.util.stream.Collectors;

public class EntityPresenter extends DTOPresenter {

    private final EntityDto entity;
    public EntityPresenter(EntityDto ent)
    {
        this.entity = ent;
    }
    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        String tab= "\t\t";
        return tab+"entity name: " + entity.getName() + "\n" +
                tab+"entity count: " + entity.getAmount() + "\n" +
                tab+"properties: \n" +
                entity.getProps().stream()
                        .map(prop -> new PropertyPresenter(prop, false))
                        .map(PropertyPresenter::toString)
                        .collect(Collectors.joining("\n\n")) + "\n";
    }
}
