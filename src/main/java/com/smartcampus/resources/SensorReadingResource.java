/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 *
 * @author Asus
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
  private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /** Part 4.2 - Fetch all historical readings for this sensor */
    @GET
    public Response getReadings() {
        return Response.ok(store.getReadings(sensorId)).build();
    }

    /**
     * Part 4.2 - Post a new reading.
     * Part 5.3 - Throws SensorUnavailableException if sensor is MAINTENANCE → 403.
     * Side effect: updates parent Sensor's currentValue for data consistency.
     */
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null)
            return Response.status(404).entity(Map.of("error", "Sensor not found: " + sensorId)).build();

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()))
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is under MAINTENANCE and cannot accept new readings. " +
                "Restore the sensor to ACTIVE status first.");

        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus()))
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE and cannot accept new readings.");

        if (reading.getId() == null || reading.getId().isBlank())
            reading.setId(UUID.randomUUID().toString());
        if (reading.getTimestamp() == 0)
            reading.setTimestamp(System.currentTimeMillis());

        store.addReading(sensorId, reading);

        // Side effect: keep parent sensor's currentValue in sync
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201)
                .header("Location", "/api/v1/sensors/" + sensorId + "/readings/" + reading.getId())
                .entity(reading).build();
    }
}  

