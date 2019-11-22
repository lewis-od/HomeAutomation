package uk.co.lewisodriscoll.haclient.helper;

import java.awt.*;

public class BrightnessHelper {

    private static final int MIN_BRIGHTNESS = 0x02;
    private static final int MAX_BRIGHTNESS = 0x1B;

    public static int percentageToEasybulb(int percentage) {
        float fraction = percentage / 100.0f;
        return (int) (fraction * (MAX_BRIGHTNESS - MIN_BRIGHTNESS)) + MIN_BRIGHTNESS;
    }
    
}
