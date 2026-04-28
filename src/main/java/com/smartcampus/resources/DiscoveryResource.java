/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
/**
 *
 * @author Asus
 */
public class DiscoveryResource {
     @GET
    public Response discover() {
        Map<String, Object> response = new HashMap<>();
        response.put("api", "Smart Campus Sensor and Room Management API");
        response.put("version", "1.0.0");
        response.put("status", "operational");
        response.put("contact", Map.of(
            "administrator", "Campus IT Infrastructure",
            "email", "smartcampus@university.ac.uk",
            "module", "5COSC022W Client-Server Architectures"
        ));
        response.put("resources", Map.of(
            "rooms",   "/api/v1/rooms",
            "sensors", "/api/v1/sensors"
        ));
        response.put("_links", Map.of(
            "self",    "/api/v1",
            "rooms",   "/api/v1/rooms",
            "sensors", "/api/v1/sensors"
        ));
        return Response.ok(response).build();
    }
}
