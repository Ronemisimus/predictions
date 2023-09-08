package predictions.execution.grid;

public class Coordinate {
    private Integer x;
    private Integer y;

    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer distance(Coordinate location) {
        return Math.abs(x - location.getX()) + Math.abs(y - location.getY());
    }

    @Override
    public String toString() {
        return "Coordinate(" + x + ", " + y + ")";
    }
}
