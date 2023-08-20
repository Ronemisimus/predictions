package predictions.exception;

public class RepeatNameException extends Throwable {

    private boolean environmentVariable;
    private String variableName;

    private String entityName;
    public RepeatNameException(String entityName, String prdName, boolean b) {
        this.variableName = prdName;
        this.environmentVariable = b;
        this.entityName = entityName;
    }

    public boolean isEnvironmentVariable() {
        return environmentVariable;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getEntityName() {
        return entityName;
    }

    @Override
    public String toString() {
        return "RepeatNameException{" +
                "environmentVariable=" + environmentVariable +
                ", variableName='" + variableName + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
