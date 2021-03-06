package uk.co.lewisodriscoll.haclient.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.lewisodriscoll.haclient.model.HaAction;

import javax.jms.JMSException;

import java.io.IOException;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueueListenerServiceTest {

    private static final String MESSAGE_JSON = "{ \"service\": \"easybulb\", \"action\": \"TurnOn\" }";

    @Mock
    private ActionIngestionService ingestionService;

    @InjectMocks
    private QueueListenerService listenerService;

    @Before
    public void setup() {
        this.listenerService = new QueueListenerService(ingestionService);
    }

    @Test
    public void testReceivesMessage() throws JMSException, IOException {
        listenerService.ingestAction(MESSAGE_JSON);

        verify(ingestionService, times(1)).ingest(HaAction.fromJson(MESSAGE_JSON));
    }

}
