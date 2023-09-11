package dto.subdto.read.dto;

public class FileSelectionDto implements dto.DTO{
    private final boolean fullPathError;

    private final String filePath;
    private final boolean fileExists;
    private final boolean isFile;
    private final boolean isXML;

    private FileSelectionDto (Builder builder) {
        this.filePath = builder.filePath;
        this.fullPathError = builder.fullPathError;
        this.fileExists = builder.fileExists;
        this.isFile = builder.isFile;
        this.isXML = builder.isXML;
    }

    public boolean isFullPathError() {
        return fullPathError;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public boolean isFile() {
        return isFile;
    }

    public boolean isXML() {
        return isXML;
    }

    public static class Builder {
        private boolean fullPathError = false;
        private final String filePath;
        private boolean fileExists = false;
        private boolean isFile = false;
        private boolean isXML = false;

        public Builder(String filePath) {
            this.filePath = filePath;
        }

        public Builder fullPathError() {
            this.fullPathError = true;
            this.fileExists = false;
            this.isFile = false;
            this.isXML = false;
            return this;
        }

        public Builder fileExists() {
            this.fullPathError = false;
            this.fileExists = false;
            this.isFile = false;
            this.isXML = false;
            return this;
        }

        public Builder isFile() {
            this.fullPathError = false;
            this.fileExists = true;
            this.isFile = false;
            return this;
        }

        public Builder isXML() {
            this.fullPathError = false;
            this.fileExists = true;
            this.isFile = false;
            this.isXML = false;
            return this;
        }

        public FileSelectionDto build() {
            return new FileSelectionDto(this);
        }
    }
}
