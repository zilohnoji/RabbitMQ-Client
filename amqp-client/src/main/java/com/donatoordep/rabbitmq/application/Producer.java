package com.donatoordep.rabbitmq.application;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Producer {

    private static final String HOST = "localhost";
    private static final String message = "Olá Worker, sou o Producer.";
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));

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