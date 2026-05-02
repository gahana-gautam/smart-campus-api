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

/**
 * Global catch-all exception mapper ensuring no raw stack trace is ever
 * returned to an API consumer.
 *
 * Implements ExceptionMapper<Throwable> to intercept any exception not handled
 * by a more specific mapper. The run time selects the mapper whose generic type
 * most closely matches the thrown exception, so Throwable is only reached as a
 * true safety net.
 *
 * Security rationale: stack traces expose class names, library versions, file
 * paths and line numbers all useful to an attacker profiling the system.
 * Full details are logged server-side only; a generic opaque message is returned
 * to the client.
 *
 * WebApplicationException instances pass through unchanged because they already
 * carry the correct HTTP status and were thrown intentionally by resource code.
 */

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
         // let these through with their own status code
        if (exception instanceof WebApplicationException wae)
            return wae.getResponse();

        // log full details server side only
        LOGGER.log(Level.SEVERE, "Unhandled error: " + exception.getMessage(), exception);
        
        // return a safe generic message to the client
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