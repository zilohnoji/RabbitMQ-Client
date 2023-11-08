package com.donatoordep.rabbitmq.application;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer2 {

    private static final String HOST = "localhost";
    private static final String EXCHANGE_NAME = "logs";
    private static final boolean durable = false;
    private static final boolean autoack = true;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println("[*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8); // Mensagem recebida
            System.out.println("\n[x] Received '" + message + "'");
            System.out.printf("Tag of channel: %d", delivery.getEnvelope().getDeliveryTag());
        };
        channel.basicConsume(queueName, autoack, deliverCallback, consumerTag -> { // Consumir a mensagem (nesse caso, imprimir na tela)
        });

    }
}