/**
 * FILE: Polygon.java
 * LOC: src.shape
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/12/2024
 *
 * DESCRIPTION: Container class for Polygons. Formed by a collection of vertices.
 */

package shape;

import java.util.ArrayList;

public class Polygon {
    /** Points follow a counterclockwise traversal of the polygon. */
    private final ArrayList<Point> points;
    /** Ear status for each vertex of the polygon. Initialized to all false. */
    private ArrayList<Boolean> ears = new ArrayList<>();

    public Polygon(ArrayList<Point> points) {
        this.points = points;
        for (Point p : points) ears.add(false);
    }

    public Polygon(Polygon otherPolygon) {
        this.points = new ArrayList<>(otherPolygon.getPoints());
        this.ears = new ArrayList<>(otherPolygon.getEars());
    }

    public ArrayList<Point> getPoints() {return points;}

    public Point getPoint(int v) {return points.get(getModIndex(v));}

    public ArrayList<Boolean> getEars() {return ears;}

    public boolean getEarStatus(int v) {return ears.get(getModIndex(v));}

    public int getSize() {return points.size();}

    public void setEarStatus(boolean ear, int v) {ears.set(getModIndex(v), ear);}

    public void removeVertex(int v) {
        points.remove(v);
        ears.remove(v);
    }

    /**
     * Get the index modded by the polygon size to mimic its cyclical nature.
     * @param index input index.
     * @return index modded by the polygon size (negatives index from the end).
     */
    private int getModIndex(int index) {
        int modIndex = index % getSize();
        if (modIndex >= 0) return modIndex;
        else return index + modIndex;
    }

}
