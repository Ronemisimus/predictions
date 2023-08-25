package console.menu.menu;

import console.menu.MenuManagerImpl;
import console.menu.option.EntityOption;
import console.menu.option.MenuItem;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.world.EntityDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntityMenu extends MenuManagerImpl {

    private EntityMenu(List<MenuItem> options, List<Boolean> canCloseMenu) {
        super(options, canCloseMenu);
    }

    public static EntityMenu getInstance(EntityListDto entityListDto)
    {
        List<MenuItem> options = new ArrayList<>();
        entityListDto.getEntities().stream().map(EntityOption::new).forEach(options::add);
        List<Boolean> canClose = IntStream.range(0,options.size()).mapToObj(i->true).collect(Collectors.toList());
        return new EntityMenu(options,canClose);
    }

    public EntityDto chosenEntity() {
        EntityOption chosenOption = getOptions().stream().map(op -> (EntityOption)op).filter(EntityOption::isChosen).findFirst().get();
        return chosenOption.getEnt();
    }
}
