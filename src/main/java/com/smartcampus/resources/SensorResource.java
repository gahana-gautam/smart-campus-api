/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Asus
 */

/**
 * JAX-RS resource managing /api/v1/sensors and delegating /readings sub-requests.
 *
 * Filtered retrieval design: the optional ?type= query parameter is implemented
 * via @QueryParam rather than a path segment because query parameters are optional
 * by nature, do not alter the resource identity, and can be combined freely
 * without requiring additional path templates.
 *
 * Referential integrity: when registering a sensor, roomId is validated against
 * the live room collection. A missing room throws LinkedResourceNotFoundException
 * mapped to HTTP 422 more accurate than 404 because the endpoint was found;
 * the problem is a broken reference inside the payload.
 */

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

  
// GET /sensors return all sensors, filter by type if ?type= is provided.

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.getSensors().values());
        // apply the filter if the client sent one
        if (type != null && !type.isBlank())
            result = result.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        return Response.ok(result).build();
    }

  
// POST /sensors register a new sensor
     
    @POST
    public Response createSensor(Sensor sensor) {
        
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank())
            return Response.status(400).entity(Map.of("error", "Sensor id is required.")).build();
     
        if (store.getSensor(sensor.getId()) != null)
            return Response.status(409).entity(Map.of("error", "Sensor already exists: " + sensor.getId())).build();
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank())
            return Response.status(400).entity(Map.of("error", "roomId is required.")).build();
        
        Room room = store.getRoom(sensor.getRoomId());
        if (room == null)
            throw new LinkedResourceNotFoundException(
                "roomId '" + sensor.getRoomId() + "' does not exist. " +
                "Sensor cannot be registered to a non-existent room.");
        
    
        if (sensor.getStatus() == null || sensor.getStatus().isBlank())
            sensor.setStatus("ACTIVE");

        store.saveSensor(sensor);
       
        room.getSensorIds().add(sensor.getId());

        return Response.status(201)
                .header("Location", "/api/v1/sensors/" + sensor.getId())
                .entity(sensor).build();
    }

   // GET /sensors/{sensorId} return a single sensor
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null)
            return Response.status(404).entity(Map.of("error", "Sensor not found: " + sensorId)).build();
        return Response.ok(sensor).build();
    }

    
    // no HTTP verb here this is a sub-resource locator
    // JAX-RS uses this to hand off /readings requests to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        if (store.getSensor(sensorId) == null)
            throw new WebApplicationException(
                Response.status(404)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", "Sensor not found: " + sensorId))
                    .build());
        return new SensorReadingResource(sensorId);
    }
}
