/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

/**
 *
 * @author Asus
 */
import com.smartcampus.filters.LoggingFilter;
import com.smartcampus.mappers.GlobalExceptionMapper;
import com.smartcampus.mappers.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.mappers.RoomNotEmptyExceptionMapper;
import com.smartcampus.mappers.SensorUnavailableExceptionMapper;
import com.smartcampus.resources.DiscoveryResource;
import com.smartcampus.resources.RoomResource;
import com.smartcampus.resources.SensorResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Logger;

/**
 * Application entry point for the Smart Campus REST API.
 *
 * Bootstraps an embedded Grizzly HTTP server programmatically,
 * eliminating the need for an external servlet container such as Tomcat or GlassFish.
 *
 * All JAX-RS components are registered explicitly via ResourceConfig rather than
 * class path scanning to avoid SmartCampusApplication's @ApplicationPath annotation
 * creating a second conflicting application context at the same base path.
 *
 * The versioned base path /api/v1 is embedded in BASE_URI so that all resource
 * @Path annotations are relative to this root an industry-standard practice
 * that allows future versions to coexist without breaking existing clients.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    /**
     * Versioned base URI. The trailing slash is required by Grizzly.
     * Binding to 0.0.0.0 makes the server reachable on all network interfaces.
     */
    
    public static final String BASE_URI = "http://0.0.0.0:8080/api/v1";

    public static void main(String[] args) throws Exception {
         
        ResourceConfig config = new ResourceConfig();
      
        
       
        config.register(DiscoveryResource.class);
        config.register(RoomResource.class);
        config.register(SensorResource.class);
        
       
        config.register(RoomNotEmptyExceptionMapper.class);
        config.register(LinkedResourceNotFoundExceptionMapper.class);
        config.register(SensorUnavailableExceptionMapper.class);
        config.register(GlobalExceptionMapper.class);
         
      
        config.register(LoggingFilter.class);

        
        config.register(JacksonFeature.class);
        
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
