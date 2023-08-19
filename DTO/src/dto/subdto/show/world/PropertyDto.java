package dto.subdto.show.world;

public class PropertyDto {
    private final String type;

    private final String name;

    private final Comparable<?> from;
    private final Comparable<?> to;

    private final Boolean randomInit;

    public PropertyDto(String type, String name, Comparable<?> from, Comparable<?> to, Boolean randomInit) {
        this.type = type;
        this.name = name;
        this.from = from;
        this.to = to;
        this.randomInit = randomInit;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Comparable<?> getFrom() {
        return from;
    }

    public Comparable<?> getTo() {
        return to;
    }

    public Boolean isRandomInit() {
        return randomInit;
    }
}
