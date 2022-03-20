package de.skyslycer.hmcwraps.circle;

public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Point build(double x, double y) {
        return new Point(x, y);
    }

}
