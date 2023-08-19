package console.menu.menu;

import console.menu.MenuManagerImpl;
import console.menu.option.MenuItem;
import console.menu.option.PropertyOption;
import dto.subdto.show.world.PropertyDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PropertyMenu extends MenuManagerImpl {

    private PropertyMenu(List<MenuItem> options, List<Boolean> canCloseMEnu) {
        super(options, canCloseMEnu);
    }

    public static PropertyMenu getInstance(List<PropertyDto> props)
    {
        List<MenuItem> options = new ArrayList<>();
        props.stream().map(PropertyOption::new).forEach(options::add);
        List<Boolean> canClose = IntStream.range(0, options.size()).mapToObj(i->true).collect(Collectors.toList());
        return new PropertyMenu(options,canClose);
    }

    public PropertyDto chosenProperty() {
        PropertyOption chosenOption = getOptions().stream()
                .map(po -> (PropertyOption)po)
                .filter(PropertyOption::isChosen)
                .findFirst().get();
        return chosenOption.getDto();
    }
}
