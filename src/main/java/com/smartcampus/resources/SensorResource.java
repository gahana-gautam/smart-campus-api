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

// * Part 3 - Sensor Operations


@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

  
//     * Part 3.2 - List sensors with optional ?type= filter.

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isBlank())
            result = result.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        return Response.ok(result).build();
    }

  
//     * Part 3.1 - Register a sensor.
     
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

   
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null)
            return Response.status(404).entity(Map.of("error", "Sensor not found: " + sensorId)).build();
        return Response.ok(sensor).build();
    }

    
//    Part 4.1 - Sub-Resource Locator (no HTTP verb annotation).
     
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
