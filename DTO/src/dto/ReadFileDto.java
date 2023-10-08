package dto;

import dto.subdto.read.dto.EntityErrorDto;
import dto.subdto.read.dto.EnvironmentErrorDto;
import dto.subdto.read.dto.FileSelectionDto;
import dto.subdto.read.dto.TerminationBadDto;
import dto.subdto.read.dto.rule.RuleErrorDto;

public class ReadFileDto implements DTO{
    private final FileSelectionDto fileSelectionDto;
    private final boolean matchesSchema;
    private final boolean badThreadCountError;
    private final Boolean gridSizeError;
    private final int gridWidth;
    private final int gridHeight;
    private final EnvironmentErrorDto environmentErrorDto;
    private final EntityErrorDto entityErrorDto;
    private final RuleErrorDto ruleErrorDto;
    private final TerminationBadDto terminationBadDto;
    private final boolean fileLoaded;
    private final String name;

    private ReadFileDto(Builder builder) {
        this.fileSelectionDto = builder.fileSelectionDto;
        this.matchesSchema = builder.matchesSchema;
        this.badThreadCountError = builder.badThreadCountError;
        this.gridSizeError = builder.gridSizeError;
        this.gridWidth = builder.gridWidth;
        this.gridHeight = builder.gridHeight;
        this.environmentErrorDto = builder.environmentErrorDto;
        this.entityErrorDto = builder.entityErrorDto;
        this.ruleErrorDto = builder.ruleErrorDto;
        this.terminationBadDto = builder.terminationBadDto;
        this.fileLoaded = builder.fileLoaded;
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String name;
        // Add more default values and parameters as needed
        private FileSelectionDto fileSelectionDto = null;
        private Boolean matchesSchema = false;
        private Boolean badThreadCountError = false;
        public Boolean gridSizeError = false;
        public int gridWidth;
        public int gridHeight;

        private EnvironmentErrorDto environmentErrorDto = null;
        private EntityErrorDto entityErrorDto = null;
        private RuleErrorDto ruleErrorDto = null;
        private TerminationBadDto terminationBadDto = null;
        private Boolean fileLoaded = false;
        public Builder() {}

        public Builder fileSelectionError(FileSelectionDto fileSelectionDto) {
            this.fileSelectionDto = fileSelectionDto;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder terminationError(TerminationBadDto terminationBadDto) {
            this.terminationBadDto = terminationBadDto;
            matchesSchema = true;
            return this;
        }

        public Builder matchesSchema(boolean matchesSchema) {
            this.matchesSchema = matchesSchema;
            return this;
        }

        public Builder fileLoaded() {
            this.fileLoaded = true;
            return this;
        }

        public Builder environmentError(EnvironmentErrorDto environmentErrorDto) {
            this.environmentErrorDto = environmentErrorDto;
            return this;
        }

        public Builder entityError(EntityErrorDto entityErrorDto) {
            this.entityErrorDto = entityErrorDto;
            return this;
        }



        public ReadFileDto build() {
            return new ReadFileDto(this);
        }

        public Builder badThreadCountError() {
            badThreadCountError = true;
            return this;
        }

        public Builder gridSizeError(int gridWidth, int gridHeight) {
            this.gridSizeError = true;
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            return this;
        }

        public Builder ruleError(RuleErrorDto build) {
            this.ruleErrorDto = build;
            return this;
        }
    }

    public FileSelectionDto getFileSelectionDto() {
        return fileSelectionDto;
    }

    public boolean isMatchesSchema() {
        return matchesSchema;
    }

    public boolean isBadThreadCountError() {
        return badThreadCountError;
    }

    public EnvironmentErrorDto getEnvironmentErrorDto() {
        return environmentErrorDto;
    }

    public EntityErrorDto getEntityErrorDto() {
        return entityErrorDto;
    }

    public TerminationBadDto getTerminationBadDto() {
        return terminationBadDto;
    }

    public boolean isFileLoaded() {
        return fileLoaded;
    }

    public Boolean getGridSizeError() {
        return gridSizeError;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public RuleErrorDto getRuleErrorDto() {
        return ruleErrorDto;
    }
}
