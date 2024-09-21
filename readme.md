# Solace Request/Reply Messaging System

This project demonstrates how to implement a basic request/reply messaging system using Solace PubSub+ APIs and Java Message Service (JMS). The system consists of two components:

1. **BasicRequestor**: A client that sends a request message to a queue and waits for a reply.
2. **BasicReplier**: A client that listens to the request queue, processes the request, and sends back a reply.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Solace Account Setup](#solace-account-setup)
- [Creating a Messaging Service in Solace Cloud](#creating-a-messaging-service-in-solace-cloud)
- [Project Setup](#project-setup)
- [Configuring Application Properties](#configuring-application-properties)
- [Running the Application in an IDE](#running-the-application-in-an-ide)
- [Troubleshooting](#troubleshooting)

## Prerequisites

Before starting, ensure you have the following installed on your machine:

- Java Development Kit (JDK) 11 or later.
- Maven (for managing dependencies and building the project).
- A Solace PubSub+ Cloud account.
- IntelliJ IDEA or any preferred IDE.

## Solace Account Setup

1. **Sign up for a Solace PubSub+ Cloud Account**
    - Go to [Solace PubSub+ Cloud](https://console.solace.cloud/) and create a free account.
    - Once registered, log in to your account.

## Creating a Messaging Service in Solace Cloud

1. **Create a Messaging Service**
    - In the Solace PubSub+ Cloud dashboard, go to **Cluster Manager** and click on **Create Service**.
    - Choose **Messaging** as the service type.
    - Choose a cloud provider and region (e.g., AWS and your preferred region).
    - Name your messaging service (e.g., `solace-request-reply-service`).
    - Wait for the service to be provisioned, which may take a few minutes.

2. **Create a Message VPN and Client Profile**
    - Once your service is provisioned, go to **Message VPNs** and create a Message VPN. For example, name it `solace-request-reply-vpn`.
    - Navigate to the **Clients** section of your Message VPN, and create a client profile with the following details:
        - **Client Username**: You can choose any name (e.g., `solace-cloud-client`).
        - **Client Password**: Set a strong password for your client.

3. **Get Connection Details**
    - In your Solace dashboard, under the **Connect** tab of your messaging service, you'll find the connection details:
        - **Host URL** (e.g., `tcps://mr-connection-xyz.messaging.solace.cloud:55443`)
        - **VPN Name** (e.g., `solace-request-reply-vpn`)
        - **Client Username** and **Password**

## Project Setup

1. **Download the Project**
    - Download or clone the project from the source repository.

2. **Open the Project in Your IDE**
    - Open your preferred IDE (e.g., IntelliJ IDEA).
    - Open the project folder from your IDE.

3. **Install Dependencies**
    - Ensure that Maven is set up correctly in your IDE. The necessary dependencies for Solace PubSub+ and JMS will be managed automatically by Maven.

## Configuring Application Properties

1. **Configure the `application.properties` File**

   The application uses an `application.properties` file to store Solace-specific configuration details. You need to modify this file with your own credentials.

2. **Location of the Properties File**

   The file should be located in the `src/main/resources/` directory.

   Open the `application.properties` file and update the following values based on your Solace setup:

    - `solace.host=YOUR_SOLACE_HOST`
    - `solace.vpn=YOUR_VPN_NAME`
    - `solace.username=YOUR_CLIENT_USERNAME`
    - `solace.password=YOUR_CLIENT_PASSWORD`

## Running the Application in an IDE

1. **Running `BasicRequestor`**

    - Open the `BasicRequestor` class in your IDE.
    - Run the class by clicking the green run button in your IDE or right-click the class and select **Run**.
    - This will connect to Solace, send a request message to the `RequestQueue`, and wait for a reply.

2. **Running `BasicReplier`**

    - Open the `BasicReplier` class in your IDE.
    - Run the class by clicking the green run button or right-click and select **Run**.
    - The `BasicReplier` will listen to the `RequestQueue` for incoming messages, process them, and send a reply back.

   Make sure you have both `BasicRequestor` and `BasicReplier` running simultaneously for the request/reply messaging system to work.

## Troubleshooting

- **Timeout Errors**: Ensure that your Solace credentials and connection information are correct in the `application.properties` file. Also, check that the queue names are correct and that the queues are active in Solace.

- **Invalid Correlation ID**: If the correlation ID doesn't match, verify that the `JMSCorrelationID` and `JMSReplyTo` fields are correctly set in your code.

- **Maven Dependency Issues**: If you face issues with missing dependencies, refresh the Maven project or use the **Maven** tool window in your IDE to reload dependencies.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
# SolaceMessagingAssignment
