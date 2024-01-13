/**
 * FILE: Diagonal.java
 * LOC: src.shape
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/12/2024
 *
 * DESCRIPTION: Container class for Diagonals. Contains an initial and terminal vertex.
 */

package shape;

public class Diagonal1 {
    private final int v1;
    private final int v2;

    public Diagonal1(int v1, int v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public int getV1() {return this.v1;}

    public int getV2() {return this.v2;}

    @Override
    public String toString() {
        return "[" + v1 + "," + v2 + "]";
    }

}
