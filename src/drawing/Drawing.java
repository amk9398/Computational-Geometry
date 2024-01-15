/**
 * FILE: Drawing.java
 * LOC: src.drawing
 *
 * AUTHOR: Aaron Kersten, aaronkersten21@gmail.com
 * DATE: 1/12/2024
 *
 * DESCRIPTION: Uses JavaFX to draw the points, lines, shapes and other geometric elements.
 */

package drawing;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import shape.Diagonal;
import shape.Point;
import shape.Polygon;

public class Drawing {

    /**
     * Draws a line from the first points p1 to the second point p2.
     * @param group the JavaFX Group that the line will be added to.
     * @param p1 the initial point.
     * @param p2 the terminal point.
     */
    public static void drawLine(Group group, Point p1, Point p2) {
        drawLine(group, p1, p2, 1);
    }

    /**
     * Draws a line from the first points p1 to the second point p2.
     * @param group the JavaFX Group that the line will be added to.
     * @param p1 the initial point.
     * @param p2 the terminal point.
     * @param strokeWidth width of the line. default is 1.
     */
    public static void drawLine(Group group, Point p1, Point p2, int strokeWidth) {
        Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        line.setStrokeWidth(strokeWidth);
        group.getChildren().add(line);
    }

    /**
     * Draws a diagonal.
     * @param group the JavaFX Group that the line will be added to.
     * @param diagonal the diagonal to be drawn.
     */
    public static void drawDiagonal(Group group, Diagonal diagonal) {
        drawLine(group, diagonal.getP1(), diagonal.getP2());
    }

    /**
     * Draws a polygon with Polygon class.
     * @param group the JavaFX Group that the line will be added to.
     * @param polygon the polygon to be drawn.
     */
    public static void drawPolygon(Group group, Polygon polygon, int strokeWidth) {
        int v1 = 0;
        int v2 = 1;
        do {
            drawLine(group, polygon.getPoint(v1), polygon.getPoint(v2), strokeWidth);
            v1 = (v1 + 1) % polygon.getSize();
            v2 = (v2 + 1) % polygon.getSize();
        } while (v1 > 0);
    }

    public static void drawPolygon(Group group, Polygon polygon) {
        drawPolygon(group, polygon, 2);
    }

}
