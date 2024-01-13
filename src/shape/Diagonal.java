/**
 * FILE: Diagonal.java
 * LOC: src.shape
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/13/2024
 *
 * DESCRIPTION: Container class for Diagonals. Contains an initial and terminal point.
 */

package shape;

public class Diagonal {
    private final Point p1;
    private final Point p2;

    public Diagonal(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {return p1;}

    public Point getP2() {return p2;}

    @Override
    public String toString() {
        return "[" + p1 + ", " + p2 + "]";
    }
}
