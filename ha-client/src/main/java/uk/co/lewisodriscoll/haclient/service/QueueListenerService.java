package uk.co.lewisodriscoll.haclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import uk.co.lewisodriscoll.haclient.model.HaAction;

import javax.jms.JMSException;
import java.io.IOException;

@Service
public class QueueListenerService {

    private final ActionIngestionService ingestionService;

    @Autowired
    public QueueListenerService(ActionIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    private final Logger log = LoggerFactory.getLogger(QueueListenerService.class);

    @JmsListener(destination = "home-automation")
    public void ingestAction(final String requestJSON) throws JMSException {
        log.trace("Message received from queue");
        try {
            HaAction action = HaAction.fromJson(requestJSON);
            ingestionService.ingest(action);
        } catch (IOException e) {
            log.error("Encountered an error whilst parsing message: " + requestJSON, e);
            throw new JMSException("Encountered an error whilst parsing message");
        }
    }

}
