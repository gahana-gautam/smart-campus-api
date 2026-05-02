/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.filters;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;
/**
 *
 * @author Asus
 */

/**
 * JAX-RS container filter providing automatic request and response logging.
 *
 * Implements both ContainerRequestFilter and ContainerResponseFilter in one class
 * to keep the cross-cutting logging concern cohesive. The request filter runs
 * before the resource method is invoked; the response filter runs after the
 * response entity is set but before it is sent to the client.
 *
 * Why a filter rather than per-method logging?
 * Centralizing logging here follows the Separation of Concerns principle.
 * Adding Logger calls inside every resource method couples observability with
 * business logic, risks accidental omission, and makes future changes require
 * edits across every endpoint rather than this single class.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    
    // runs before the request hits any resource method
    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        LOGGER.info(String.format("[REQUEST]  %s %s",
            req.getMethod(),
            req.getUriInfo().getRequestUri()));
    }

    // runs after the response is ready to send
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        LOGGER.info(String.format("[RESPONSE] %s %s -> HTTP %d",
            req.getMethod(),
            req.getUriInfo().getRequestUri(),
            res.getStatus()));
    }
}
