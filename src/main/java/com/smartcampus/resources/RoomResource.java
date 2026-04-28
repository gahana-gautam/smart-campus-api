/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

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
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();

    /** Part 2.1 - List all rooms (full objects, not just IDs) */
    @GET
    public Response getAllRooms() {
        List<Room> list = new ArrayList<>(store.getRooms().values());
        return Response.ok(list).build();
    }
     /** Part 2.1 - Create a room, returns 201 + Location header */
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
    /** Part 2.1 - Get a single room by ID */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null)
            return Response.status(404).entity(Map.of("error", "Room not found: " + roomId)).build();
        return Response.ok(room).build();
    }
       /**
     * Part 2.2 - Delete a room.
     * Business rule: blocked if sensors are still assigned → throws RoomNotEmptyException → 409.
     *
     * Idempotency: First call returns 204 (deleted). Subsequent identical calls return 404
     * (already gone). Server state is identical after the first call — room is absent.
     * HTTP defines idempotency by server-side effect, not response code, so this is correct.
     */
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
                "Room '" + roomId + "' cannot be deleted — it still has active sensors assigned. " +
                "Remove or reassign all sensors first.");

        store.deleteRoom(roomId);
        return Response.noContent().build();
    }
}

