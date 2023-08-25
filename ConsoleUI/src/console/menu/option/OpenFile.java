package console.menu.option;

import console.EngineApi;
import console.dto.presenter.DTOPresenter;
import console.menu.ReadInput;

public class OpenFile implements MenuItem{
    private boolean atLeastOneFileLoaded;

    public OpenFile() {
        this.atLeastOneFileLoaded = false;
    }

    @Override
    public boolean run() {
        System.out.println("Please enter the path of the file to load");
        DTOPresenter rfp = EngineApi.getInstance().readFile(ReadInput.getString());
        System.out.println(rfp);
        this.atLeastOneFileLoaded = rfp.success();
        return atLeastOneFileLoaded;
    }

    @Override
    public String toString() {
        return "Read world from xml file";
    }
}
