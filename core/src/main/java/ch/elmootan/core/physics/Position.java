package ch.elmootan.core.physics;

public class Position {
    // Position verticale.
    private double x;

    // Position horizontale.
    private double y;

    public Position(){}

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override
    public String toString()
    {
        return "Position : (" + x + ";" + y + ")";
    }
}