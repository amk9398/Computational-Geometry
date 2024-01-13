/**
 * FILE: Point.java
 * LOC: src.shape
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/12/2024
 *
 * DESCRIPTION: Container class for Points. Contains an X and Y coordinate.
 */

package shape;

public class Point {
    private double X_COORDINATE;
    private double Y_COORDINATE;

    public Point(double x, double y) {
        this.X_COORDINATE = x;
        this.Y_COORDINATE = y;
    }

    public double getX() {
        return X_COORDINATE;
    }

    public double getY() {
        return Y_COORDINATE;
    }

    public void setX(double x) {
        X_COORDINATE = x;
    }

    public void setY(double y) {
        Y_COORDINATE = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point p)) return false;
        return X_COORDINATE == p.getX() && Y_COORDINATE == p.getY();
    }

    @Override
    public String toString() {
        return "(" + X_COORDINATE + "," + Y_COORDINATE + ")";
    }
}
