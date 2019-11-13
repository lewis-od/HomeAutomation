package uk.co.lewisodriscoll.haclient.helper;

import java.awt.*;

public class ColourHelper {

    private static int MIN_COLOUR = 0x00;
    private static int MAX_COLOUR = 0xFF;

    private static int MIN_BRIGHTNESS = 0x02;
    private static int MAX_BRIGHTNESS = 0x1B;

    public static String colourToString(Color colour) {
        float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        hsb[0] = hsb[0] * 360.0f;
        return String.format("%.4G, %.4G, %.4G", hsb[0], hsb[1], hsb[2]);
    }

    public static Color stringToColour(String colourString) throws NumberFormatException {
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

    public static float[] getHSB(Color colour) {
        return Color.RGBtoHSB(colour.getRed(), colour.getBlue(), colour.getGreen(), null);
    }

    public static float getHue(Color colour) {
        return getHSB(colour)[0];
    }

    public static float getSaturation(Color colour) {
        return getHSB(colour)[1];
    }

    public static int colourToEasybulbHue(Color colour) {
        float actualHue = getHue(colour);
        float easybulbHue = (5 / 3f - actualHue) % 1f;

        return (int) (easybulbHue * MAX_COLOUR);
    }

    public static int percentageToEasybulb(int percentage) {
        float fraction = percentage / 100.0f;
        return (int) (fraction * (MAX_BRIGHTNESS - MIN_BRIGHTNESS)) + MIN_BRIGHTNESS;
    }
}
