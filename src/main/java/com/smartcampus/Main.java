/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

/**
 *
 * @author Asus
 */
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Logger;
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://0.0.0.0:8080/";

    public static void main(String[] args) throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.packages("com.smartcampus");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

        LOGGER.info("Smart Campus API started.");
        LOGGER.info("Access the API at: http://localhost:8080/api/v1");
        LOGGER.info("Press CTRL+C to stop the server.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down server...");
            server.shutdownNow();
        }));

        Thread.currentThread().join();
    }
}
