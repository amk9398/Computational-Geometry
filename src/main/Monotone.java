/**
 * FILE: Monotone.java
 * LOC: main
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/15/2024
 *
 * DESCRIPTION: Partition a polygon into monotone pieces.
 * NOTE: No two vertices of the polygon can have the same y coordinate.
 */


package main;

import drawing.Drawing;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shape.Point;
import shape.Polygon;
import shape.Segment;
import java.util.ArrayList;
import static main.Trapezoidalization.*;
import static main.Triangulation.areaPoly2;
import static main.Triangulation.diagonal;
import static tools.PolygonTools.readPolygon;

public class Monotone extends Application {

    public static void main(String[] args) {Application.launch(args);}

    @Override
    public void start(Stage stage) {
        // read polygon data from file
        Polygon polygon = readPolygon("src/data/poly7.txt");
        if (polygon == null) {
            System.out.println("Could not read polygon file.");
            return;
        }

        Group group = new Group();

        // calculate the polygon's area
        System.out.println("The polygon's area is " + (areaPoly2(polygon) / 2));

        // draw monotone partition
        drawMonotone(group, polygon);

        // window setup
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setTitle("Trapezoidalization");
        stage.show();
    }

    /**
     * Creates a diagonal between two vertices. If the diagonal is not valid, return null.
     * @param polygon polygon being examined.
     * @param v the first vertex.
     * @param vp the second vertex.
     * @return a diagonal segment if valid; otherwise, null.
     */
    public static Segment partition(Polygon polygon, int v, int vp) {
        if (diagonal(polygon, v, vp)) {
            return new Segment(polygon.getPoint(v), polygon.getPoint(vp));
        }
        return null;
    }

    /**
     * Creates the first possible diagonal between a given vertex and another in ascending or descending order.
     * @param polygon polygon being examined.
     * @param vertices sorted list of vertices.
     * @param index index of first vertex.
     * @param asc direction to look for next vertex.
     * @return a diagonal segment.
     */
    public static Segment partition(Polygon polygon, ArrayList<Integer> vertices, int index, boolean asc) {
        Segment s;
        int c = 1;
        do {
            int vp = asc ? vertices.get(index - c++) : vertices.get(index + c++);
            s = partition(polygon, vertices.get(index), vp);
        } while (s == null);
        return s;
    }

    /**
     * Updates the list of edges pierced by the line running through the current vertex.
     * @param polygon the shape being examined.
     * @param pierced the list of pierced edges.
     * @param vertices the sorted list of vertices.
     * @param index position of current vertex in vertices.
     */
    public static void updatePierced(Polygon polygon, ArrayList<Segment> pierced, ArrayList<Integer> vertices, int index) {
        int v = vertices.get(index);
        Point p = polygon.getPoint(v);
        Point p0 = polygon.getPoint(v - 1);
        Point p1 = polygon.getPoint(v + 1);
        Segment s0 = new Segment(p0, p);
        Segment s1 = new Segment(p, p1);
        int v0 = pierced.indexOf(s0);
        int v1 = pierced.indexOf(s1);
        if (v0 == -1 && v1 == -1) {
            pierced.add(findVertex(pierced, p), p0.getX() < p1.getX() ? s1 : s0);
            pierced.add(findVertex(pierced, p), p0.getX() < p1.getX() ? s0 : s1);
        } else if (v0 == -1) {
            pierced.set(v1, s0);
        } else if (v1 == -1) {
            pierced.set(v0, s1);
        } else {
            pierced.remove(s0);
            pierced.remove(s1);
        }
    }

    /**
     * Creates a list of segments that partition the polygon into monotone pieces.
     * A monotone polygon with respect to the vertical has a monotone ordering of vertices when projected onto the
     * vertical.
     * @param polygon the shape being partitioned.
     * @return a list of segments that partition the polygon.
     */
    public static ArrayList<Segment> monotonePartition(Polygon polygon) {
        ArrayList<Integer> vertices = sortVerticesY(polygon);
        ArrayList<Segment> segments = new ArrayList<>();
        ArrayList<Segment> pierced = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            int v = vertices.get(i);
            Point p = polygon.getPoint(v);
            Point p0 = polygon.getPoint(v - 1);
            Point p1 = polygon.getPoint(v + 1);
            int v0 = pierced.indexOf(new Segment(p0, p));
            int v1 = pierced.indexOf(new Segment(p, p1));
            updatePierced(polygon, pierced, vertices, i);
            if (v0 == -1 && v1 == -1 && reflex(p, p1, p0)) {
                segments.add(partition(polygon, vertices, i, true));
            }
            else if (v0 != -1 && v1 != -1 && reflex(p, p1, p0)) {
                segments.add(partition(polygon, vertices, i, false));
            }
        }
        return segments;
    }

    /**
     * Create a list of polygons that make up the component monotone pieces of the polygon.
     * @param polygon the shape being partitioned.
     * @return a list of monotone polygons.
     */
    public static ArrayList<Polygon> monotone(Polygon polygon) {
        ArrayList<Segment> partitions = monotonePartition(polygon);
        ArrayList<Polygon> polygons = new ArrayList<>();
        Polygon copyPolygon = new Polygon(polygon);
        for (Segment segment : partitions) {
            int v1 = polygon.getVertexNumber(segment.getP1());
            int v2 = polygon.getVertexNumber(segment.getP2());
            int vmin = Math.min(v1, v2);
            int vmax = Math.max(v1, v2);
            polygons.add(new Polygon(polygon.getPoints().subList(vmin, 1 + vmax)));
            for (int i = vmin + 1; i < vmax; i++) {
                copyPolygon.getPoints().remove(polygon.getPoint(i));
            }
        }
        polygons.add(copyPolygon);
        return polygons;
    }

    /**
     * Calculate and draw a monotone partition for the polygon.
     * @param group the JavaFX Group being drawn to.
     * @param polygon the shape being partitioned.
     */
    public static void drawMonotone(Group group, Polygon polygon) {
        ArrayList<Polygon> polygons = monotone(polygon);
        for (Polygon monotone : polygons)  Drawing.drawPolygon(group, monotone, 2);
    }
}
