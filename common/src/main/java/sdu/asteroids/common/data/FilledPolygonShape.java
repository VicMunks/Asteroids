package sdu.asteroids.common.data;

public class FilledPolygonShape implements IShape {
    private final double[] points;
    private final double[] color;

    public FilledPolygonShape(double[] points, double[] color) {
        this.points = points;
        this.color = color;
    }

    @Override
    public double[] getPoints() { return points; }

    @Override
    public double[] getColorRGBA() { return color; }
}
