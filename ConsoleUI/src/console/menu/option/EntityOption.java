package console.menu.option;

import dto.subdto.show.world.EntityDto;

public class EntityOption implements MenuItem{
    private final EntityDto ent;
    private boolean chosen;

    public EntityOption(EntityDto ent) {
        this.ent = ent;
        this.chosen = false;
    }

    @Override
    public boolean run() {
        this.chosen=true;
        return true;
    }

    public EntityDto getEnt() {
        return ent;
    }

    public boolean isChosen() {
        return chosen;
    }

    @Override
    public String toString() {
        return ent.getName();
    }
}
