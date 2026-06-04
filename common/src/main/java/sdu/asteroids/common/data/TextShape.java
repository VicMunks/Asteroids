package sdu.asteroids.common.data;

public class TextShape implements IShape {
    private String text;
    private final double fontSize;
    private final double[] color;

    public TextShape(String text, double fontSize, double[] color) {
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
    }

    @Override
    public double[] getPoints() { return new double[0]; }

    @Override
    public double[] getColorRGBA() { return color; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public double getFontSize() { return fontSize; }
}
