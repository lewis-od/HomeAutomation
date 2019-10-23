package uk.co.lewisodriscoll.haclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
            return new HaResponse("Unknown host");
        } catch (IOException e) {
            return new HaResponse("IO exception occurred");
        }

        return new HaResponse("Success");
    }

    private void sendBytes(byte[] bytesToSend) throws IOException, UnknownHostException {
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
