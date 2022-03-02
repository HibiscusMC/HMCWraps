package de.skyslycer.hmcwraps.preview;

public class Point<T> {

    private final T x;
    private final T z;

    public Point(T x, T z) {
        this.x = x;
        this.z = z;
    }

    public static <T> Point<T> build(T x, T z) {
        return new Point<>(x, z);
    }

    public T getX() {
        return x;
    }

    public T getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "x=" + x + ", z=" + z;
    }
}
