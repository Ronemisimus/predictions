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
            return "Bad function expression in " + dto.getProperyName() + " fix to continue";
        }
        if(dto.isMatchesSchema())
        {
            return "Does not match xml schema";
        }
        if (dto.isEnvironmentVariableRepeatedKey())
        {
            return "Environment variable repeated " + dto.getProperyName();
        }
        else if(dto.getEntityName()!=null && dto.getProperyName()!=null)
        {
            return dto.getEntityName() + " has repeated property: " + dto.getProperyName();
        }
        if(dto.isPropertyDoesNotExistInEntity())
        {
            return dto.getEntityName() + " has repeated property: " + dto.getProperyName();
        }
        if(dto.isIncreaseDecreaseCalculationParametersNumerical())
        {
            return dto.getProperyName() + " has invalid parameter types: " + dto.getProperyType();
        }
        if(dto.isMissingEntity())
        {
            return "entity does not exist: " + dto.getEntityName();
        }
        return "error";
    }
}
