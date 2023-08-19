package console.menu.option;

import dto.subdto.show.world.PropertyDto;

public class PropertyOption implements MenuItem {

    private boolean chosen;
    private PropertyDto dto;

    public PropertyOption(PropertyDto dto) {
        this.dto = dto;
        this.chosen = false;
    }

    @Override
    public boolean run() {
        chosen = true;
        return true;
    }

    public boolean isChosen() {
        return chosen;
    }

    public PropertyDto getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return dto.getName() + " of type " + dto.getType();
    }
}
