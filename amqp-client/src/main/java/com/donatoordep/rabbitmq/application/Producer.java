package com.donatoordep.rabbitmq.application;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static final String QUEUE_NAME = "users_email";
    private static final String HOST = "localhost";
    private static final String message = "Olá Worker, sou o Producer.";
    private static final String EXCHANGE_NAME = "logs";
    private static final boolean durable = false;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {


            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
            channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, message.getBytes());

            System.out.printf("[X] Enviado %s", message);
        } catch (ConnectException connectException) {
            System.out.println("""
                    \u001b[01;31m
                    Não foi possível se conectar.
                    Verifique sua conexão com o servidor.\u001b[0m
                    """);
        }
    }
}