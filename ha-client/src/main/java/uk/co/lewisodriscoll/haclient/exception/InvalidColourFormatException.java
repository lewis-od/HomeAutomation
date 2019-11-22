package uk.co.lewisodriscoll.haclient.exception;

public class InvalidColourFormatException extends Exception {

    public InvalidColourFormatException(String colourString) {
        super("Invalid colour format: " + colourString);
    }

}
