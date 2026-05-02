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
import java.util.Map;
import java.util.UUID;
/**
 *
 * @author Asus
 */

/**
 * JAX-RS sub-resource handling reading history at:
 * GET  /api/v1/sensors/{sensorId}/readings
 * POST /api/v1/sensors/{sensorId}/readings
 *
 * Not registered as a top-level resource. Instantiated and returned by the
 * sub-resource locator in SensorResource, which is the idiomatic JAX-RS
 * Sub-Resource Locator pattern. The run time passes the unmatched portion of
 * the URI (/readings) to this class for further routing, enabling clean
 * separation between sensor management and reading management.
 *
 * State constraint: only ACTIVE sensors accept readings. MAINTENANCE or OFFLINE
 * sensors throw SensorUnavailableException mapped to HTTP 403 Forbidden.
 *
 * Side effect on POST: a successful submission updates the parent sensor's
 * currentValue to always reflect the latest recorded measurement.
 */

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
  private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /sensors/{sensorId}/readings return all readings for this sensor
    @GET
    public Response getReadings() {
        return Response.ok(store.getReadings(sensorId)).build();
    }

     // POST /sensors/{sensorId}/readings add a new reading
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
        
        // fill in id and timestamp if the client didn't provide them
        if (reading.getId() == null || reading.getId().isBlank())
            reading.setId(UUID.randomUUID().toString());
        if (reading.getTimestamp() == 0)
            reading.setTimestamp(System.currentTimeMillis());

        store.addReading(sensorId, reading);

        // update the sensor's current value to match the latest reading
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201)
                .header("Location", "/api/v1/sensors/" + sensorId + "/readings/" + reading.getId())
                .entity(reading).build();
    }
}  

