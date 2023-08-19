package dto;

public class ReadFileDto implements DTO{

    private final boolean fullPathError;
    private final boolean fileExists;

    private final boolean isFile;

    private final boolean isXML;

    private final boolean matchesSchema;

    private final boolean fileLoaded;

    public ReadFileDto(boolean fullPathError,
                       boolean fileExists,
                       boolean isFile,
                       boolean isXML,
                       boolean matchesSchema,
                       boolean fileLoaded) {
        this.fullPathError = fullPathError;
        this.fileExists = fileExists;
        this.isFile = isFile;
        this.isXML = isXML;
        this.matchesSchema = matchesSchema;
        this.fileLoaded = fileLoaded;
    }

    public boolean isFileLoaded() {
        return fileLoaded;
    }
}
