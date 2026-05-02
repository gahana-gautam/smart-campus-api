/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;

import com.smartcampus.exceptions.SensorUnavailableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

/**
 *
 * @author Asus
 */

/**
 * Maps SensorUnavailableException to HTTP 403 Forbidden.
 *
 * The sensor exists and was found (ruling out 404), but the server refuses
 * the reading submission based on the sensor's current state. RFC 9110 permits
 * 403 when the server declines to fulfill a request based on server-side policy,
 * which accurately describes this state-machine constraint.
 */

@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException e) {
        return Response.status(403)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                    "status", 403,
                    "error",  "Forbidden",
                    "message", e.getMessage(),
                    "hint",   "Sensor must be ACTIVE to accept new readings."
                )).build();
    }
}
