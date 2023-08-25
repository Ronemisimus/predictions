package console.dto.presenter;

import dto.ReadFileDto;

public class ReadFilePresenter extends DTOPresenter {

    private final ReadFileDto dto;
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
        if (dto.isFile())
        {
            return "Not a file";
        }
        if(dto.isFileExists())
        {
            return "File does not exist";
        }
        if(dto.isXML())
        {
            return "Not an XML file";
        }
        if(dto.isFullPathError())
        {
            return "Not a full path - must be absolute path";
        }
        if(dto.isBadFunctionExpression())
        {
            return "Bad function expression in " + dto.getPropertyName() + " fix to continue";
        }
        if(dto.isMatchesSchema())
        {
            return "Does not match xml schema";
        }
        if (dto.isEnvironmentVariableRepeatedKey())
        {
            return "Environment variable repeated " + dto.getPropertyName();
        }
        else if(dto.getEntityName()!=null && dto.getPropertyName()!=null)
        {
            return dto.getEntityName() + " has repeated property: " + dto.getPropertyName();
        }
        if(dto.isPropertyDoesNotExistInEntity())
        {
            return dto.getEntityName() + " has repeated property: " + dto.getPropertyName();
        }
        if(dto.isIncreaseDecreaseCalculationParametersNumerical())
        {
            return dto.getPropertyName() + " has invalid parameter types: " + dto.getPropertyType();
        }
        if(dto.isMissingEntity())
        {
            return "entity does not exist: " + dto.getEntityName();
        }
        return "error";
    }
}
