/**
 * FILE: Trapezoidalization.java
 * LOC: main
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/14/2024
 *
 * DESCRIPTION: Create a trapezoidalization of a polygon by partitioning it into trapezoids and triangles.
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
import java.util.List;
import static main.Triangulation.*;
import static tools.PolygonTools.readPolygon;

public class Trapezoidalization extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // read polygon data from file
        Polygon polygon = readPolygon("src/data/poly7.txt");
        if (polygon == null) {
            System.out.println("Could not read polygon file.");
            return;
        }

        Group group = new Group();

        // draw polygon
        Drawing.drawPolygon(group, polygon);

        // calculate the polygon's area
        System.out.println("The polygon's area is " + (areaPoly2(polygon) / 2));

        // draw trapezoidalization
        drawTrapezoidalization(group, polygon);

        // window setup
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setTitle("Trapezoidalization");
        stage.show();
    }

    /**
     * Sort vertices by y coordinate ascending.
     * @param polygon the shape being trapezoidilated.
     * @return a list of integers where each entry is the index of its associated vertex of the polygon.
     */
    public static ArrayList<Integer> sortVerticesY(Polygon polygon) {
        // copy polygon points list
        ArrayList<Point> vertices = new ArrayList<>(polygon.getPoints());

        // sort by y coordinate
        // use x coordinate to break ties
        vertices.sort((o1, o2) -> {
            if (o1.getY() - o2.getY() != 0) return (int) (o1.getY() - o2.getY());
            else return (int) (o1.getX() - o2.getX());
        });

        // return mapped list with associated index of each vertex
        return new ArrayList<>(vertices.stream().map(polygon::getVertexNumber).toList());
    }

    /**
     * Find the x coordinate of the intersection between a segment and a horizontal.
     * @param segment line segment.
     * @param y height of the horizontal.
     * @return the x coordinate of the intersection point.
     */
    public static double findIntersectionX(Segment segment, double y) {
        Point p1 = segment.getP1();
        Point p2 = segment.getP2();

        // if line is vertical return x coord
        if (p1.getX() == p2.getX()) return p1.getX();

        // otherwise, calculate slope and intersection point
        double m = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        return (y - p1.getY() + m * p1.getX()) / m;
    }

    /**
     * Determine where the current vertex lies in the list of pierced edges.
     * @param pierced list of edges pierced by the horizontal running through p.
     * @param p current vertex being examined.
     * @return number of edges preceding p in the pierced edges list.
     */
    public static int findVertex(ArrayList<Segment> pierced, Point p) {
        List<Double> piercedX = pierced.stream().map(s -> findIntersectionX(s, p.getY())).toList();
        for (int i = 0; i < piercedX.size(); i++) {
            if (p.getX() <= piercedX.get(i)) return i;
        }
        return piercedX.size();
    }

    public static boolean reflex(Point a, Point b, Point c) {
        return !leftOn(a, b, c);
    }

    public static Segment horizontal(ArrayList<Segment> pierced, Point p, int index) {
        return new Segment(p, new Point(findIntersectionX(pierced.get(index), p.getY()), p.getY()));
    }

    /**
     * Calculate a trapezoidalization for the given polygon. This partitioning can consist of trapezoids or triangles.
     * @param polygon the shape being trapezoidilated.
     * @return a list of segments comprising the trapezoidalization.
     */
    public static ArrayList<Segment> trapezoidalization(Polygon polygon) {
        ArrayList<Segment> segments = new ArrayList<>();
        ArrayList<Segment> pierced = new ArrayList<>();
        Point p, p0, p1;
        Segment s0, s1;
        int index, v0, v1;
        for (int v : sortVerticesY(polygon)) {
            p = polygon.getPoint(v);
            p0 = polygon.getPoint(v - 1);
            p1 = polygon.getPoint(v + 1);
            s0 = new Segment(p0, p);
            s1 = new Segment(p, p1);
            index = findVertex(pierced, p);
            v0 = pierced.indexOf(s0);
            v1 = pierced.indexOf(s1);
            if (v0 == -1 && v1 == -1) {
                if (reflex(p, p1, p0)) {
                    segments.add(horizontal(pierced, p, index - 1));
                    segments.add(horizontal(pierced, p, index));
                }
                pierced.add(index, s0.getP1().getX() < s1.getP1().getX() ? s1 : s0);
                pierced.add(index, s0.getP1().getX() < s1.getP1().getX() ? s0 : s1);
            } else if (v0 == -1) {
                segments.add(horizontal(pierced, p, index - 1));
                pierced.set(v1, s0);
            } else if (v1 == -1) {
                segments.add(horizontal(pierced, p, index + 1));
                pierced.set(v0, s1);
            } else {
                pierced.remove(s0);
                pierced.remove(s1);
                if (reflex(p, p1, p0)) {
                    segments.add(horizontal(pierced, p, index - 1));
                    segments.add(horizontal(pierced, p, index));
                }
            }
        }
        return segments;
    }

    /**
     * Calculate and draw a trapezoidalization for the polygon.
     * @param group the JavaFX Group that the trapezoidalization will be added to.
     * @param polygon the shape being trapezoidilated.
     */
    public static void drawTrapezoidalization(Group group, Polygon polygon) {
        ArrayList<Segment> segments = trapezoidalization(polygon);
        for (Segment segment : segments) {
            Drawing.drawLine(group, segment.getP1(), segment.getP2());
        }
    }

}
