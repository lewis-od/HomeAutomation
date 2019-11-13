package uk.co.lewisodriscoll.haclient.service;

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
    private ActionIngestionService ingestionService;

    private Logger log = Logger.getLogger(QueueListenerService.class);

    @JmsListener(destination = "home-automation")
    public void ingestAction(String requestJSON) throws JMSException {
        log.info("Message received from queue");
        try {
            HaAction action = HaAction.fromJson(requestJSON);
            ingestionService.ingest(action);
        } catch (IOException e) {
            log.error("Encountered an error whilst parsing message: " + requestJSON, e);
            throw new JMSException("Encountered an error whilst parsing message");
        }
    }

}
