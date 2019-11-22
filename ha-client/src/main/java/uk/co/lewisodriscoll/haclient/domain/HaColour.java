package uk.co.lewisodriscoll.haclient.domain;

import java.awt.*;

public class HaColour {

    private static final int MIN_HUE = 0x00;
    private static final int MAX_HUE = 0xFF;

    private Color colour;

    public HaColour(String colourString) {
        this.colour = stringToColour(colourString);
    }

    public HaColour(Color colour) {
        this.colour = colour;
    }

    public HaColour(int red, int green, int blue) {
        this.colour = new Color(red, green, blue);
    }

    public float getHue() {
        return getHSB()[0];
    }

    public float getSaturation() {
        return getHSB()[1];
    }

    public int getEasybulbHue() {
        float actualHue = getHue();
        float easybulbHue = (5 / 3f - actualHue) % 1f;

        return (int) (easybulbHue * MAX_HUE);
    }

    public String toString() {
        float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        hsb[0] = hsb[0] * 360.0f;
        return String.format("%.4G, %.4G, %.4G", hsb[0], hsb[1], hsb[2]);
    }

    private Color stringToColour(String colourString) throws NumberFormatException {
        String[] parts = colourString.split(", ");

        if (parts.length != 3) {
            throw new NumberFormatException();
        }

        float[] hsb = {0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            hsb[i] = Float.parseFloat(parts[i]);
        }

        return Color.getHSBColor(hsb[0] / 360.0f, hsb[1], hsb[2]);
    }

    private float[] getHSB() {
        return Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
    }

}
