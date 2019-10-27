package uk.co.lewisodriscoll.haclient.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@Configuration
@EnableJms
public class JmsConfig {

    private SQSConnectionFactory connectionFactory =
        SQSConnectionFactory.builder()
            .withRegion(Region.getRegion(Regions.EU_WEST_2))
            .withAWSCredentialsProvider(new ClasspathPropertiesFileCredentialsProvider())
            .build();

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.connectionFactory);
        // Allows us to refer to SQS queues by name
        factory.setDestinationResolver(new DynamicDestinationResolver());
        // Minimum of 1 listener, scale up to 1 listener (i.e. only 1 listener)
        factory.setConcurrency("1-1");
        // Delete messages from queue after service method is complete
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return factory;
    }

}
