package console.dto.presenter.subpresenter;

import console.dto.presenter.DTOPresenter;
import dto.subdto.show.world.PropertyDto;

public class PropertyPresenter extends DTOPresenter {

    private PropertyDto prop;
    private boolean environment;
    public PropertyPresenter(PropertyDto prop, boolean environment) {
        this.prop = prop;
        this.environment = environment;
    }
    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        Comparable<?> from = prop.getFrom();
        Comparable<?> to = prop.getTo();
        String tab = environment? "\t\t": "\t\t\t\t";
        return tab+"property name: " + prop.getName() + "\n" +
                tab+"type: "+ prop.getType() + "\n" +
                tab+(from==null ? "" : "from: " + prop.getFrom() + "\n") +
                tab+(to==null ? "" : "to: " + prop.getTo() + "\n") +
                tab+(environment?"":"random init: " + prop.isRandomInit());
    }
}
