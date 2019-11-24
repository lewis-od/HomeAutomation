package uk.co.lewisodriscoll.haclient.helper;

import org.junit.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BrightnessHelperTest {

    @Test
    public void testConvertsMinBrightnessCorrectly() {
        int brightness = BrightnessHelper.percentageToEasybulb(0);
        then(brightness).isEqualTo(0x02);
    }

    @Test
    public void testConvertsMaxBrightnessCorrectly() {
        int brightness = BrightnessHelper.percentageToEasybulb(100);
        then(brightness).isEqualTo(0x1B);
    }

    @Test
    public void testConvertsMidBrightnessCorrectly() {
        int brightness = BrightnessHelper.percentageToEasybulb(50);
        int expectedBrightness = (0x1B + 0x02) / 2;
        then(brightness).isEqualTo(expectedBrightness);
    }

}
