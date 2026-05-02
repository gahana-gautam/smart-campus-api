/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;
import com.smartcampus.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Asus
 */

/**
 * JAX-RS resource managing the /api/v1/rooms collection.
 *
 * Implements full CRUD for Room resources. Each method maps to a standard HTTP
 * verb and returns the most semantically appropriate status code per RFC 9110.
 *
 * Life cycle note: JAX-RS creates a new RoomResource instance per request.
 * Shared state lives in the singleton DataStore, not in instance fields.
 *
 * Business rule safe deletion: A room may only be deleted when it has no
 * sensors assigned. This prevents orphaned Sensor records whose roomId would
 * point to a non-existent parent. Violations throw RoomNotEmptyException
 * mapped to HTTP 409 Conflict.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();

    
    @GET
    public Response getAllRooms() {
        List<Room> list = new ArrayList<>(store.getRooms().values());
        return Response.ok(list).build();
    }
     
    @POST
    public Response createRoom(Room room) {
        
        if (room == null || room.getId() == null || room.getId().isBlank())
            return Response.status(400).entity(Map.of("error", "Room id is required.")).build();
         
        if (store.getRoom(room.getId()) != null)
            return Response.status(409).entity(Map.of("error", "Room already exists: " + room.getId())).build();
      
        if (room.getSensorIds() == null)
            room.setSensorIds(new ArrayList<>());
        store.saveRoom(room);
        return Response.status(201)
                .header("Location", "/api/v1/rooms/" + room.getId())
                .entity(room).build();
    }
   
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null)
            return Response.status(404).entity(Map.of("error", "Room not found: " + roomId)).build();
        return Response.ok(room).build();
    }
    
    
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null)
            return Response.status(404).entity(Map.of("error", "Room not found: " + roomId)).build();
       
        
        boolean hasSensors = store.getSensors().values().stream()
                .anyMatch(s -> roomId.equals(s.getRoomId()));

        if (hasSensors || !room.getSensorIds().isEmpty())
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' cannot be deleted it still has active sensors assigned. " +
                "Remove or reassign all sensors first.");

        store.deleteRoom(roomId);
        return Response.noContent().build();
    }
}

