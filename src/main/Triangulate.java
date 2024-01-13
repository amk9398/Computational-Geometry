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
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static tools.PolygonTools.readPolygon;

public class Triangulate extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Polygon polygon = readPolygon("src/data/poly2.txt");
        if (polygon == null) {
            System.out.println("Could not read polygon file.");
            return;
        }
        Group group = new Group();
        Drawing.drawPolygon(group, polygon);
        drawTriangulation(group, polygon);
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setTitle("Triangulation");
        stage.show();
    }

    public static double area2(Point a, Point b, Point c) {
        return - ((b.getX() - a.getX()) * (c.getY() - a.getY()) -
                  (c.getX() - a.getX()) * (b.getY() - a.getY()));
    }

    public static double areaPoly2(Polygon polygon) {
        double sum = 0;
        int ind = 1;
        do {
            sum += area2(polygon.getPoint(0), polygon.getPoint(ind), polygon.getPoint(ind + 1));
            ind++;
        } while (ind + 1 < polygon.getSize());
        return sum;
    }

    public static boolean left(Point a, Point b, Point c) {
        return area2(a, b, c) > 0;
    }

    public static boolean leftOn(Point a, Point b, Point c) {
        return area2(a, b, c) >= 0;
    }

    public static boolean collinear(Point a, Point b, Point c) {
        return area2(a, b, c) == 0;
    }

    public static boolean intersectionProper(Point a, Point b, Point c, Point d) {
        if (collinear(a, b, c) || collinear(a, b, d) || collinear(c, d, a) || collinear(c, d, b)) {
            return false;
        }
        return (left(a, b, c) ^ left(a, b, d)) && (left(c, d, a) ^ left(c, d, b));
    }

    public static boolean between(Point a, Point b, Point c) {
        if (!collinear(a, b, c)) return false;
        if (a.getX() != b.getX()) {
            return ((a.getX() <= c.getX()) && (c.getX() <= b.getX())) ||
                    ((a.getX() >= c.getX()) && (c.getX() >= b.getX()));
        } else {
            return ((a.getY() <= c.getY()) && (c.getY() <= b.getY())) ||
                    ((a.getY() >= c.getY()) && (c.getY() >= b.getY()));
        }
    }

    public static boolean intersect(Point a, Point b, Point c, Point d) {
        if (intersectionProper(a, b, c, d)) return true;
        else return between(a, b, c) || between(a, b, c) || between(c, d, a) || between(c, d, b);
    }

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

    public static boolean inCone(Polygon polygon, int v1, int v2) {
        Point a = polygon.getPoint(v1);
        Point b = polygon.getPoint(v2);
        Point a0 = polygon.getPoint(v1 - 1);
        Point a1 = polygon.getPoint(v1 + 1);
        if (leftOn(a, a1, a0)) {
            return left(a, b, a0) && left(b, a, a1);
        }
        return !(leftOn(a, b, a1) && leftOn(b, a, a0));
    }

    public static boolean diagonal(Polygon polygon, int v1, int v2) {
        return inCone(polygon, v1, v2) && inCone(polygon, v2, v1) && diagonalie(polygon, v1, v2);
    }

    public static void earInit(Polygon polygon) {
        int ind = 0;
        do {
            polygon.setEarStatus(diagonal(polygon, ind - 1, ind + 1), ind);
            ind++;
        } while(ind < polygon.getSize());
    }

    public static ArrayList<Diagonal> triangulate(Polygon polygon) {
        ArrayList<Diagonal> diagonals = new ArrayList<>();
        double area2 = 0;
        int v0, v1, v2, v3, v4;
        earInit(polygon);
        while (polygon.getSize() > 3) {
            v2 = 0;
            do {
                if (polygon.getEarStatus(v2)) {
                    v0 = v2 - 2;
                    v1 = v2 - 1;
                    v3 = v2 + 1;
                    v4 = v2 + 2;
                    diagonals.add(new Diagonal(polygon.getPoint(v1), polygon.getPoint(v3)));
                    area2 += area2(polygon.getPoint(v1), polygon.getPoint(v2), polygon.getPoint(v3));
                    polygon.setEarStatus(diagonal(polygon, v0, v3), v1);
                    polygon.setEarStatus(diagonal(polygon, v1, v4), v3);
                    polygon.removeVertex(v2);
                    break;
                }
                v2++;
            } while (v2 < polygon.getSize());
        }

        return diagonals;
    }

    public static void drawTriangulation(Group group, Polygon polygon) {
        Polygon copyPolygon = new Polygon(polygon);
        ArrayList<Diagonal> diagonals = triangulate(copyPolygon);
        for (Diagonal diagonal : diagonals) {
            Drawing.drawDiagonal(group, diagonal);
        }
    }

}
