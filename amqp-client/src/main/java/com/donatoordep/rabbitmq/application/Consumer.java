package com.donatoordep.rabbitmq.application;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static final String QUEUE_NAME = "users_email";
    private static final String HOST = "localhost";
    private static final String EXCHANGE_NAME = "logs";
    private static final boolean durable = false;
    private static final boolean autoack = false;
    private static final int prefetchCount = 3;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        channel.basicQos(prefetchCount); // Número total de acknowledgements negativos aceitos
        System.out.println("[*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8); // Mensagem recebida
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("\n[x] Received '" + message + "'");
            System.out.printf("Tag of channel: %d", delivery.getEnvelope().getDeliveryTag());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true); // Confirmação positiva referente à entrega da mensagem // Mensagem descartada
        };
        Thread.sleep(5000);
        channel.basicConsume(QUEUE_NAME, autoack, deliverCallback, consumerTag -> { // Consumir a mensagem (nesse caso, imprimir na tela)
        });
    }
}