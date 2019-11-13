package uk.co.lewisodriscoll.haclient.helper;

import java.awt.*;

public class ColourHelper {

    private static int MIN_COLOUR = 0x00;
    private static int MAX_COLOUR = 0xFF;

    private static int MIN_BRIGHTNESS = 0x02;
    private static int MAX_BRIGHTNESS = 0x1B;

    public static int colourToEasybulbHue(Color colour) {
        float[] hsb = Color.RGBtoHSB(
            colour.getRed(),
            colour.getGreen(),
            colour.getBlue(),
            null
        );

        float actualHue = hsb[0];

        float easybulbHue = (5 / 3f - actualHue) % 1f;

        return (int) (easybulbHue * MAX_COLOUR);
    }

    public static String colourToString(Color colour) {
        float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        return String.format("%.4G, %.4G, %.4G", hsb[0], hsb[1], hsb[2]);
    }

    public static int percentageToEasybulb(int percentage) {
        float fraction = percentage / 100.0f;
        return (int) (fraction * (MAX_BRIGHTNESS - MIN_BRIGHTNESS)) + MIN_BRIGHTNESS;
    }
}
