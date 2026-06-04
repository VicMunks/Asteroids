package sdu.asteroids.common.data;

public class PolygonShape implements IShape {
    private double[] points;
    private double[] color;

    public PolygonShape(double[] points, double[] color) {
        this.points = points;
        this.color = color;
    }

    @Override
    public double[] getPoints() { return points; }

    @Override
    public double[] getColorRGBA() { return color; }
}
