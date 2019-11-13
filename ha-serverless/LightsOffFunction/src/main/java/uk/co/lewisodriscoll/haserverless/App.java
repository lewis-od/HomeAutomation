package uk.co.lewisodriscoll.haserverless;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQSClient;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, Object> {

    private SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
            new ProviderConfiguration(),
            AmazonSQSClient.builder()
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .withRegion(Regions.EU_WEST_2)
                    .build()
    );

    public Object handleRequest(final Object input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        try {
            SQSConnection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("home-automation");
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("{ \"service\": \"easybulb\", \"action\": \"off\" }");
            producer.send(message);
        } catch (JMSException e) {
            return new GatewayResponse("{ \"status\": \"ERROR\" }", headers, 500);
        }

        return new GatewayResponse("{ \"status\": \"SUCCESS\" }", headers, 200);
    }

}
