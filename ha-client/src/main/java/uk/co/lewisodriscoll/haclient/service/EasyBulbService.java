package uk.co.lewisodriscoll.haclient.service;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.helper.ColourHelper;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class EasyBulbService {
    private static final int CODE_ON = 0x42;
    private static final int CODE_OFF = 0x41;
    private static final int CODE_WHITE = 0xC2;
    private static final int CODE_COLOUR = 0x40;

    @Value("${easybulb.ip}")
    private String easybulbBoxIp;

    @Value("${easybulb.port}")
    private int easybulbBoxPort;

    private Logger log = Logger.getLogger(EasyBulbService.class);

    public void performAction(HaAction action) {
        switch (action.getAction()) {
            case "TurnOn":
                turnLightOn();
                break;

            case "TurnOff":
                turnLightOff();
                break;

            case "SetColor":
                String[] parts = action.getValue().split(", ");

                if (parts.length != 3) {
                    log.error("Invalid colour: " + action.getAction());
                }

                float[] hsb = {0.0f, 0.0f, 0.0f};
                try {
                    for (int i = 0; i < 3; i++) {
                        hsb[i] = Float.parseFloat(parts[i]);
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid float format");
                    log.error(e);
                }
                Color colour = Color.getHSBColor(hsb[0] / 360.0f, hsb[1], hsb[2]);

                // Easybulb doesn't handle low saturation well - turn white
                if (hsb[1] < 0.5f) {
                    turnLightWhite();
                } else {
                    setLightColour(colour);
                }
                break;

            default:
                log.error("Unknown action: " + action.getAction());
        }
    }

    public HaResponse turnLightOn() {
        return sendCode(CODE_ON);
    }

    public HaResponse turnLightOff() {
        return sendCode(CODE_OFF);
    }

    public HaResponse turnLightWhite() {
        return sendCode(CODE_WHITE);
    }

    public HaResponse setLightColour(Color colour) {
        int hue = ColourHelper.getEasybulbHue(colour);
        return sendCode(CODE_COLOUR, hue);
    }

    private HaResponse sendCode(int code, int value) {
        byte[] paddedCode = { (byte) code, (byte) value, 0x55 };
        try {
            sendBytes(paddedCode);
        } catch (UnknownHostException e) {
            log.error("Unknown host.");
            return new HaResponse(HaResponse.Status.ERROR, "Unknown host");
        } catch (IOException e) {
            log.error("IO exception occurred.", e);
            return new HaResponse(HaResponse.Status.ERROR, "IO exception occurred");
        }

        return new HaResponse(HaResponse.Status.SUCCESS, "Action completed");
    }

    private HaResponse sendCode(int code) {
        return sendCode(code, 0x00);
    }

    private void sendBytes(byte[] bytesToSend) throws IOException {
        DatagramPacket packet = new DatagramPacket(
            bytesToSend,
            bytesToSend.length,
            InetAddress.getByName(easybulbBoxIp),
            easybulbBoxPort
        );

        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }

}
