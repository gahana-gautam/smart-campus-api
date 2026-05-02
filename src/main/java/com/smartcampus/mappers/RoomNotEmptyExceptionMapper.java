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

/**
 * Maps RoomNotEmptyException to HTTP 409 Conflict.
 *
 * The DELETE request is valid and the room exists, but the operation conflicts
 * with the current server state: the room cannot be removed while sensors still
 * reference it. RFC 9110 defines 409 as correct when a request conflicts with
 * the current state of the target resource.
 *
 * The hint field in the response body actively guides the client toward the
 * corrective action required before retrying the deletion.
 */
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
