package uk.co.lewisodriscoll.haclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class EasybulbService {
    private static final int CODE_ON = 0x42;
    private static final int CODE_OFF = 0x41;
    private static final int CODE_WHITE = 0xC2;
    private static final int CODE_COLOUR = 0x40;
    private static final int CODE_BRIGHTNESS = 0x4E;

    @Value("${easybulb.ip}")
    private String easybulbBoxIp;

    @Value("${easybulb.port}")
    private int easybulbBoxPort;

    private Logger log = LoggerFactory.getLogger(EasybulbService.class);

    public HaResponse turnLightOn() {
        return sendCode(CODE_ON);
    }

    public HaResponse turnLightOff() {
        return sendCode(CODE_OFF);
    }

    public HaResponse turnLightWhite() {
        return sendCode(CODE_WHITE);
    }

    public HaResponse setLightColour(int hue) {
        return sendCode(CODE_COLOUR, hue);
    }

    public HaResponse setLightBrightness(int brightness) {
        return sendCode(CODE_BRIGHTNESS, brightness);
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
