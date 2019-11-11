package uk.co.lewisodriscoll.haclient.helper;

import java.awt.*;

public class ColourHelper {

    private static int MIN_COLOUR = 0x00;
    private static int MAX_COLOUR = 0xFF;

    public static int getEasybulbHue(Color colour) {
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

}
