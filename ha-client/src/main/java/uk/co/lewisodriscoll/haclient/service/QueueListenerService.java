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

    private Logger log = Logger.getLogger(QueueListenerService.class.toString());

    @JmsListener(destination = "home-automation")
    public void processAction(String requestJSON) throws JMSException {
        log.info("Message received.");
        try {
            HaAction action = HaAction.fromJson(requestJSON);
            log.info(action.toString());
        } catch (IOException e) {
            log.error("Encountered an error whilst parsing message: " + requestJSON, e);
            throw new JMSException("Encountered an error whilst parsing message");
        }
    }

}
