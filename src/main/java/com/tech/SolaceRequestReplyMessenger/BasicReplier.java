package com.tech.SolaceRequestReplyMessenger;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import javax.jms.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class BasicReplier {

    private static final String REQUEST_QUEUE_NAME = "RequestQueue";
    private final CountDownLatch latch = new CountDownLatch(1);

    public void run(String host, String vpn, String username, String password) throws Exception {
        System.out.printf("BasicReplier is connecting to Solace messaging at %s...%n", host);

        // Create connection factory and configure
        SolConnectionFactory factory = SolJmsUtility.createConnectionFactory();
        factory.setHost(host);
        factory.setVPN(vpn);
        factory.setUsername(username);
        factory.setPassword(password);

        // Create connection to Solace broker
        Connection connection = factory.createConnection();
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.printf("Connected to Solace VPN '%s' with username '%s'.%n", vpn, username);

        // Create request queue
        Queue requestQueue = session.createQueue(REQUEST_QUEUE_NAME);
        MessageConsumer requestConsumer = session.createConsumer(requestQueue);

        // Asynchronous message listener for incoming requests
        requestConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message request) {
                try {
                    System.out.println("Received request: " + ((TextMessage) request).getText());

                    // Retrieve the reply-to destination from the message
                    Destination replyDestination = request.getJMSReplyTo();

                    if (replyDestination != null) {
                        // Create a reply message
                        TextMessage replyMessage = session.createTextMessage("This is my reply");
                        replyMessage.setJMSCorrelationID(request.getJMSCorrelationID());

                        // Send the reply
                        MessageProducer replyProducer = session.createProducer(replyDestination);
                        replyProducer.send(replyDestination, replyMessage);
                        System.out.println("Reply sent successfully.");
                    } else {
                        System.out.println("No reply-to destination specified.");
                    }

                    latch.countDown();  // Notify main thread
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Start receiving requests
        connection.start();
        System.out.println("Awaiting request...");

        // Wait until a request is processed
        latch.await();

        // Cleanup resources
        requestConsumer.close();
        session.close();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = BasicReplier.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String host = properties.getProperty("solace.host");
        String vpn = properties.getProperty("solace.vpn");
        String username = properties.getProperty("solace.username");
        String password = properties.getProperty("solace.password");

        new BasicReplier().run(host, vpn, username, password);
    }
}
