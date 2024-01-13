package shape;

public class Diagonal {
    private Point p1;
    private Point p2;

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
