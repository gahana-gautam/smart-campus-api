/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Asus
 */

//  Part 5.4 - Global Safety Net: catches ALL Throwable instances.

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Preserve intended status codes for WebApplicationExceptions
        if (exception instanceof WebApplicationException wae)
            return wae.getResponse();

        // Log full trace server-side, never send it to the client
        LOGGER.log(Level.SEVERE, "Unhandled error: " + exception.getMessage(), exception);

        return Response.status(500)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                    "status",  500,
                    "error",   "Internal Server Error",
                    "message", "An unexpected error occurred. Contact the system administrator.",
                    "ref",     "Check server logs for full diagnostics."
                )).build();
    }
}