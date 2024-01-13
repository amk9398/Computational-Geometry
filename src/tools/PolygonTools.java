/**
 * FILE: PolygonTools.java
 * LOC: src.tools
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/13/2024
 *
 * DESCRIPTION: Tools related to polygon creation.
 */

package tools;

import shape.Point;
import shape.Polygon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class PolygonTools {

    /**
     * Read a file with polygon data and construct a new Polygon.
     * Format is x1,y1 - each line represents one vertex.
     * @param filename polygon file path name.
     * @return new Polygon built from file data. Null for empty or incorrectly formatted file.
     */
    public static Polygon readPolygon(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            ArrayList<Point> points = new ArrayList<>();
            while((line = br.readLine()) != null) {
                double x = Double.parseDouble(line.split(",")[0]);
                double y = Double.parseDouble(line.split(",")[1]);
                points.add(new Point(x, y));
            }
            return new Polygon(points);
        } catch (Exception ignored) {}
        return null;
    }

}
