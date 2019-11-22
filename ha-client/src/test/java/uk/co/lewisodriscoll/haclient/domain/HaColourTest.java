package uk.co.lewisodriscoll.haclient.domain;

import org.assertj.core.data.Offset;
import org.junit.Test;
import uk.co.lewisodriscoll.haclient.exception.InvalidColourFormatException;

import java.awt.*;

import static org.assertj.core.api.BDDAssertions.then;

public class HaColourTest {

    private static final String INVALID_COLOUR = "abcdef";
    private static final String VALID_COLOUR = "39.00, 1.00, 1.00";

    private static final Offset<Float> EPSILON = Offset.offset(0.4f);

    private static final Color givenColour = new Color(20, 80, 150);

    @Test
    public void testParsesColourString() throws InvalidColourFormatException {
        HaColour colour = new HaColour(VALID_COLOUR);

        then(colour.getHue() * 360.0f).isCloseTo(39.0f, EPSILON);
    }

    @Test(expected = InvalidColourFormatException.class)
    public void testThrowsInvalidColourFormatException() throws InvalidColourFormatException {
        HaColour colour = new HaColour(INVALID_COLOUR);
    }

    @Test
    public void testGetHue() {
        HaColour colour = new HaColour(givenColour);

        then(colour.getHue() * 360.0f).isCloseTo(212.0f, EPSILON);
    }

    @Test
    public void testGetSaturation() {
        HaColour colour = new HaColour(givenColour);

        then(colour.getSaturation() * 100.0f).isCloseTo(87.0f, EPSILON);
    }

    @Test
    public void testGetEasybulbHue() {
        HaColour colour = new HaColour(givenColour);

        then(colour.getEasybulbHue()).isEqualTo(19);
    }

}
