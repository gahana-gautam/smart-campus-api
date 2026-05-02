/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
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
 * Maps LinkedResourceNotFoundException to HTTP 422 Unable to process Entity.
 *
 * 422 is more semantically accurate than 404 here: the endpoint /api/v1/sensors
 * was found successfully. The problem is that the roomId reference inside the
 * request body cannot be resolved a semantic error in a valid payload.
 * RFC 4918 defines 422 for exactly this scenario.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {
    
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                    "status", 422,
                    "error",  "Unprocessable Entity",
                    "message", e.getMessage(),
                    "hint",   "Ensure roomId in your request body refers to an existing room."
                )).build();
    }
}