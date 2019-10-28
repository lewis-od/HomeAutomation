package uk.co.lewisodriscoll.haclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.model.HaAction;

import javax.jms.JMSException;
import java.io.IOException;

@Service
public class QueueListenerService {

    @Autowired
    private EasyBulbService easyBulbService;

    private Logger log = Logger.getLogger(QueueListenerService.class);

    @JmsListener(destination = "home-automation")
    public void ingestAction(String requestJSON) throws JMSException {
        log.info("Message received.");
        try {
            HaAction action = HaAction.fromJson(requestJSON);
            log.info(action.toString());
            processAction(action);
        } catch (IOException e) {
            log.error("Encountered an error whilst parsing message: " + requestJSON, e);
            throw new JMSException("Encountered an error whilst parsing message");
        }
    }

    private void processAction(HaAction action) {
        switch (action.getService()) {
            case "easybulb":
                easyBulbService.performAction(action);
                break;

            default:
                log.error("Unknown service name: " + action.getService());
        }
    }

}