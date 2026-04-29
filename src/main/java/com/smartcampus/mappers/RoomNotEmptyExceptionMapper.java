/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;
import com.smartcampus.exceptions.RoomNotEmptyException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;
/**
 *
 * @author Asus
 */
// Part 5.1 - Maps RoomNotEmptyException to HTTP 409 Conflict with JSON body. 
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        return Response.status(409)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                    "status", 409,
                    "error",  "Conflict",
                    "message", e.getMessage(),
                    "hint",   "Remove or reassign all sensors before deleting this room."
                )).build();
    }
}
