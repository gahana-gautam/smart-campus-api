/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 *
 * @author Asus
 */

/**
 * Thrown when a client attempts to delete a Room that still has sensors assigned.
 *
 * Enforces the referential integrity business rule: removing a room while sensors
 * still reference it via roomId would create orphaned records pointing to a
 * non-existent parent.
 *
 * Mapped to HTTP 409 Conflict by RoomNotEmptyExceptionMapper.
 * 409 is correct here because the request is valid but conflicts with the
 * current state of the resource.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) { super(message); }
}
