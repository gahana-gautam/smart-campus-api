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
 * Thrown when a reading is posted to a sensor in MAINTENANCE or OFFLINE status.
 *
 * A physically disconnected or under-maintenance sensor cannot produce valid
 * measurements, so accepting readings from it would corrupt the historical log.
 *
 * Mapped to HTTP 403 Forbidden by SensorUnavailableExceptionMapper.
 * 403 is appropriate because the sensor exists and was found (ruling out 404),
 * but the server refuses the action based on the sensor's current state.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) { super(message); }
}
