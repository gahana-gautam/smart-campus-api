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
@Provider
public class LoginFilter {
    private static final Logger LOGGER = Logger.getLogger(LoginFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        LOGGER.info(String.format("[REQUEST]  %s %s",
            req.getMethod(),
            req.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        LOGGER.info(String.format("[RESPONSE] %s %s -> HTTP %d",
            req.getMethod(),
            req.getUriInfo().getRequestUri(),
            res.getStatus()));
    }
}
