package com.tech.SolaceRequestReplyMessenger;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import javax.jms.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class BasicRequestor {

    private static final String REQUEST_QUEUE_NAME = "RequestQueue";
    private static final int REPLY_TIMEOUT_MS = 60000; // 60 seconds

    public void run(String host, String vpn, String username, String password) throws Exception {
        System.out.printf("BasicRequestor is connecting to Solace messaging at %s...%n", host);

        // Create connection factory and configure
        SolConnectionFactory factory = SolJmsUtility.createConnectionFactory();
        factory.setHost(host);
        factory.setVPN(vpn);
        factory.setUsername(username);
        factory.setPassword(password);

        // Create connection to Solace broker
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        System.out.printf("Connected to Solace VPN '%s' with username '%s'.%n", vpn, username);

        // Create the request queue
        Queue requestQueue = session.createQueue(REQUEST_QUEUE_NAME);
        MessageProducer requestProducer = session.createProducer(requestQueue);

        // Use a temporary queue for the reply
        TemporaryQueue replyQueue = session.createTemporaryQueue();
        MessageConsumer replyConsumer = session.createConsumer(replyQueue);

        // Start the connection to receive messages
        connection.start();

        // Prepare request message
        TextMessage requestMessage = session.createTextMessage("This is my request");
        String correlationId = UUID.randomUUID().toString();
        requestMessage.setJMSCorrelationID(correlationId);
        requestMessage.setJMSReplyTo(replyQueue);  // Set the reply-to queue

        // Send the request
        System.out.printf("Sending request '%s' to queue '%s'...%n", requestMessage.getText(), REQUEST_QUEUE_NAME);
        requestProducer.send(requestQueue, requestMessage);

        // Wait for the reply
        System.out.println("Request sent. Waiting for reply...");
        Message replyMessage = replyConsumer.receive(REPLY_TIMEOUT_MS);

        if (replyMessage == null) {
            throw new JMSException("Failed to receive a reply within the timeout period.");
        }

        // Verify correlation ID
        if (!replyMessage.getJMSCorrelationID().equals(correlationId)) {
            throw new JMSException("Received invalid correlation ID in reply.");
        }

        // Process reply
        if (replyMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) replyMessage;
            System.out.printf("Received reply: '%s'%n", textMessage.getText());
        } else {
            System.out.println("Received non-text message as a reply.");
        }

        // Cleanup resources
        replyConsumer.close();
        requestProducer.close();
        session.close();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = BasicRequestor.class.getClassLoader().getResourceAsStream("application.properties")) {
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

        new BasicRequestor().run(host, vpn, username, password);
    }
}
