/**
 * FILE: main.Triangulate.java
 * LOC: src
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/12/2024
 *
 * DESCRIPTION: Triangulate a polygon using traditional methods, monotone polygons, and monotone mountains.
 */

package main;

import drawing.Drawing;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shape.Diagonal;
import shape.Point;
import shape.Polygon;
import java.util.ArrayList;

import static tools.PolygonTools.readPolygon;

public class Triangulate extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // read polygon data from file
        Polygon polygon = readPolygon("src/data/poly2.txt");
        if (polygon == null) {
            System.out.println("Could not read polygon file.");
            return;
        }

        Group group = new Group();

        // draw polygon
        Drawing.drawPolygon(group, polygon);

        // calculate and draw triangulation
        drawTriangulation(group, polygon);

        // calculate the polygon's area
        System.out.println("The polygon's area is " + (areaPoly2(polygon) / 2));

        // window setup
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setTitle("Triangulation");
        stage.show();
    }

    /**
     * Calculates twice the area of a triangle.
     * The formula is derived from the concept of taking the cross product of two of the triangle's sides (namely ab
     * and ac). The cross product can then be simplified into the form below.
     * @param a the first triangle vertex.
     * @param b the second triangle vertex.
     * @param c the third triangle vertex.
     * @return Twice the area of the triangle.
     */
    public static double area2(Point a, Point b, Point c) {
        return - ((b.getX() - a.getX()) * (c.getY() - a.getY()) -
                  (c.getX() - a.getX()) * (b.getY() - a.getY()));
    }

    /**
     * Calculates twice the area of a given polygon.
     * The function takes the first point of the polygon and constructs triangles with every subsequent pair of
     * vertices. Triangles formed from the polygon's interior will have a counterclockwise ordering and will have a
     * positive area. Conversely, triangles formed from the exterior will have a negative area. The resulting sum of
     * each triangle gives the true area of the entire polygon.
     * @param polygon shape that will be examined.
     * @return Twice the area of the polygon.
     */
    public static double areaPoly2(Polygon polygon) {
        double sum = 0;
        int ind = 1;
        do {
            sum += area2(polygon.getPoint(0), polygon.getPoint(ind), polygon.getPoint(ind + 1));
            ind++;
        } while (ind + 1 < polygon.getSize());
        return sum;
    }

    /**
     * Determines whether a point is to the left of a line segment a->b.
     * If c is to the left of a->b, then the triangle abc will have a counterclockwise ordering and thus have a
     * positive area.
     * @param a the origin point of the line segment.
     * @param b the terminal point of the line segment.
     * @param c the point that is being measured.
     * @return true if c is left of the segment a->b; otherwise, false.
     */
    public static boolean left(Point a, Point b, Point c) {
        return area2(a, b, c) > 0;
    }

    /**
     * Determines whether a point is to the left of a line segment a->b or along a->b.
     * If c is to the left of or on a->b, then the triangle abc will have a counterclockwise ordering and thus have a
     * nonnegative area.
     * @param a the origin point of the line segment.
     * @param b the terminal point of the line segment.
     * @param c the point that is being measured.
     * @return true if c is left of or on the segment a->b; otherwise, false.
     */
    public static boolean leftOn(Point a, Point b, Point c) {
        return area2(a, b, c) >= 0;
    }

    /**
     * Determines whether the three points are collinear.
     * If the area of the triangle abc is 0, then the points must be collinear.
     * @param a the first point.
     * @param b the second point.
     * @param c the third point.
     * @return true if the points are collinear; otherwise false.
     */
    public static boolean collinear(Point a, Point b, Point c) {
        return area2(a, b, c) == 0;
    }

    /**
     * Determines whether the line segments a->b and c->d have a proper intersection. That is, the segments intersect,
     * but no point a or b lies and c->d and vice versa.
     * If any three points are collinear then the second condition is false. For the segments to intersect, a and b
     * must lie on opposite sides of c->d; similarly, c and d must also lie on opposite sides of a->b.
     * @param a the origin of segment a->b.
     * @param b the terminus of segment a->b.
     * @param c the origin of segment c->d.
     * @param d the terminus of segment c->d.
     * @return true if a->b and c->d have a proper intersection; otherwise, false.
     */
    public static boolean intersectionProper(Point a, Point b, Point c, Point d) {
        if (collinear(a, b, c) || collinear(a, b, d) || collinear(c, d, a) || collinear(c, d, b)) {
            return false;
        }
        return (left(a, b, c) ^ left(a, b, d)) && (left(c, d, a) ^ left(c, d, b));
    }

    /**
     * Check that a point is between the line segment a->b.
     * @param a the origin of the segment a->b.
     * @param b the terminus of the segment a->b.
     * @param c the point to be checked.
     * @return true if c is between a and b; otherwise false.
     */
    public static boolean between(Point a, Point b, Point c) {
        if (!collinear(a, b, c)) return false;

        // check for "betweenness" on X. If a->b is vertical, check on Y.
        if (a.getX() != b.getX()) {
            return ((a.getX() <= c.getX()) && (c.getX() <= b.getX())) ||
                    ((a.getX() >= c.getX()) && (c.getX() >= b.getX()));
        } else {
            return ((a.getY() <= c.getY()) && (c.getY() <= b.getY())) ||
                    ((a.getY() >= c.getY()) && (c.getY() >= b.getY()));
        }
    }

    /**
     * Determines whether line segment a->b and c->d intersect.
     * @param a the origin of segment a->b.
     * @param b the terminus of segment a->b.
     * @param c the origin of segment c->d.
     * @param d the terminus of segment c->d.
     * @return true if a->b and c->d intersect; otherwise, false.
     */
    public static boolean intersect(Point a, Point b, Point c, Point d) {
        if (intersectionProper(a, b, c, d)) return true;
        else return between(a, b, c) || between(a, b, d) || between(c, d, a) || between(c, d, b);
    }

    /**
     * Determine whether the segment between the given vertices intersects any polygon edges.
     * We can skip all edges incident to v1 and v2. For all others, we check if the diagonal v1->v2 intersects
     * the edge.
     * @param polygon the shape being triangulated.
     * @param v1 the first vertex of the diagonal.
     * @param v2 the second vertex of the diagonal.
     * @return true if there is no intersection; otherwise, false.
     */
    public static boolean diagonalie(Polygon polygon, int v1, int v2) {
        int ind = 0;
        Point a = polygon.getPoint(v1);
        Point b = polygon.getPoint(v2);
        Point c = polygon.getPoint(ind);
        do {
            Point c1 = polygon.getPoint(ind + 1);
            if (!c.equals(a) && !c1.equals(a) && !c.equals(b) && !c1.equals(b) && intersect(a, b, c, c1)) return false;
            ind++;
            c = polygon.getPoint(ind);
        } while(ind < polygon.getSize());
        return true;
    }

    /**
     * Determines whether the segment between the given vertices lies in the cone formed counterclockwise
     * from the two vertices adjacent to v1.
     * @param polygon the shape being triangulated.
     * @param v1 the first vertex of the diagonal.
     * @param v2 the second vertex of the diagonal.
     * @return true if v1->v2 lies in the cone; otherwise, false.
     */
    public static boolean inCone(Polygon polygon, int v1, int v2) {
        Point a = polygon.getPoint(v1);
        Point b = polygon.getPoint(v2);
        Point a0 = polygon.getPoint(v1 - 1);
        Point a1 = polygon.getPoint(v1 + 1);

        // checks if v1 is a convex vertex
        if (leftOn(a, a1, a0)) {
            return left(a, b, a0) && left(b, a, a1);
        }

        // else v1 is reflexive
        return !(leftOn(a, b, a1) && leftOn(b, a, a0));
    }

    /**
     * Determines whether v1->v2 is a diagonal of the polygon.
     * If v1 is in the cone around v2, v2 is in the cone around v1, and v1->v2 does not intersect any vertices
     * of the polygon, then v1->v2 is a diagonal.
     * @param polygon the shape that is being evaluated.
     * @param v1 the first vertex of the diagonal.
     * @param v2 the second vertex of the diagonal.
     * @return true if v1->v2 is a diagonal; otherwise, false.
     */
    public static boolean diagonal(Polygon polygon, int v1, int v2) {
        return inCone(polygon, v1, v2) && inCone(polygon, v2, v1) && diagonalie(polygon, v1, v2);
    }

    /**
     * Sets the ear status of each vertex of the polygon.
     * A vertex v1 is an "ear" if its adjacent vertices v0 and v2 form a diagonal.
     * @param polygon the shape being evaluated.
     */
    public static void earInit(Polygon polygon) {
        int ind = 0;
        do {
            polygon.setEarStatus(diagonal(polygon, ind - 1, ind + 1), ind);
            ind++;
        } while(ind < polygon.getSize());
    }

    /**
     * Calculate a triangulation of the given polygon by "cutting" of an ear - remove the ear vertex and save the ear's
     * diagonal. Repeat until there is only a triangle left.
     * @param polygon the shape being triangulated.
     * @return a list of diagonals that comprise the triangulation.
     */
    public static ArrayList<Diagonal> triangulate(Polygon polygon) {
        ArrayList<Diagonal> diagonals = new ArrayList<>();
        int v0, v1, v2, v3, v4;

        // initialize polygon ear status
        earInit(polygon);

        // loop until the polygon is a triangle
        while (polygon.getSize() > 3) {

            // select the first vertex of the polygon
            v2 = 0;
            do {
                // check if polygon is an ear
                if (polygon.getEarStatus(v2)) {
                    v0 = v2 - 2;
                    v1 = v2 - 1;
                    v3 = v2 + 1;
                    v4 = v2 + 2;

                    // add the diagonal between the ear vertex's preceding and succeeding vertices
                    diagonals.add(new Diagonal(polygon.getPoint(v1), polygon.getPoint(v3)));

                    // update the ear status of the adjacent vertices
                    polygon.setEarStatus(diagonal(polygon, v0, v3), v1);
                    polygon.setEarStatus(diagonal(polygon, v1, v4), v3);

                    // remove ear vertex from polygon
                    polygon.removeVertex(v2);
                    break;
                }

                // otherwise, move to the next vertex
                v2++;
            } while (v2 < polygon.getSize());
        }
        return diagonals;
    }

    /**
     * Calculate and draw triangulation for the polygon.
     * @param group JavaFX Group that the triangulation will be added to.
     * @param polygon the shape being triangulated.
     */
    public static void drawTriangulation(Group group, Polygon polygon) {
        Polygon copyPolygon = new Polygon(polygon);
        ArrayList<Diagonal> diagonals = triangulate(copyPolygon);
        for (Diagonal diagonal : diagonals) {
            Drawing.drawDiagonal(group, diagonal);
        }
    }

}
