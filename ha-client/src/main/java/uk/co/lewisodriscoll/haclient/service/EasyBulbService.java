package uk.co.lewisodriscoll.haclient.service;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.model.HaAction;
import uk.co.lewisodriscoll.haclient.model.HaResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class EasyBulbService {
    private static final int CODE_ON = 0x42;
    private static final int CODE_OFF = 0x41;

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

    private HaResponse sendCode(int code) {
        byte[] paddedCode = { (byte) code, 0x00, 0x55 };
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
