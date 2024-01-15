package shape;

public class Segment {
    /** Initial point of the segment. */
    private final Point p1;
    /** Terminal point of the segment. */
    private final Point p2;

    public Segment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {return p1;}

    public Point getP2() {return p2;}

    @Override
    public String toString() {
        return "[" + p1 + ", " + p2 + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Segment s)) return false;
        return p1.equals(s.getP1()) && p2.equals(s.getP2());
    }
}
