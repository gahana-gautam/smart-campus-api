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