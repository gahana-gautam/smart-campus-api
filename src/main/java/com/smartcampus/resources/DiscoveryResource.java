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
        Map<String, Object> meta = new HashMap<>();
        meta.put("name", "Smart Campus API");
        meta.put("version", "1.0");
        meta.put("description", "Sensor & Room Management API for the University Smart Campus");
        meta.put("contact", "admin@smartcampus.ac.uk");

        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        meta.put("resources", links);

        return Response.ok(meta).build();
    }
}
