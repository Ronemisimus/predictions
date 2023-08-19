package console.dto.presenter;

import console.dto.presenter.subpresenter.WorldPresenter;
import dto.ShowWorldDto;

public class ShowWorldPresenter extends DTOPresenter {
    private ShowWorldDto dto;

    public ShowWorldPresenter(ShowWorldDto dto) {
        this.dto = dto;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public String toString() {
        return new WorldPresenter(dto.getWorld()).toString();
    }
}
