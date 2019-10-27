package uk.co.lewisodriscoll.haclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.logging.Logger;

@Service
public class QueueListenerService {

    private Logger log = Logger.getLogger(QueueListenerService.class.toString());

    @JmsListener(destination = "home-automation")
    public void processAction(String requestJSON) throws JMSException {
        log.info("Message received.");
        System.out.println(requestJSON);
    }

}
