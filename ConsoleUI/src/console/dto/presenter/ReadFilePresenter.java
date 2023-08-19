package console.dto.presenter;

import dto.ReadFileDto;

public class ReadFilePresenter extends DTOPresenter {

    private ReadFileDto dto;
    public ReadFilePresenter(ReadFileDto dto) {
        this.dto = dto;
    }

    @Override
    public boolean success() {
        return dto.isFileLoaded();
    }

    @Override
    public String toString() {
        if (dto.isFileLoaded())
        {
            return "File loaded successfully";
        }
        return "error";
    }
}
