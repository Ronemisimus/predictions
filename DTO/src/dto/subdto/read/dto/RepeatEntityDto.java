package dto.subdto.read.dto;

import dto.DTO;

public class RepeatEntityDto implements DTO {
    private final String name;

    public RepeatEntityDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
