/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.models;

/**
 *
 * @author Asus
 */

/**
 * Domain model representing a single timestamped measurement captured by a sensor.
 *
 * SensorReading objects form the immutable historical log for each sensor.
 * Once appended, readings are never modified — this append-only semantic mirrors
 * real-world telemetry systems where data integrity requires a complete,
 * unaltered record of past measurements.
 *
 * If the client omits id or timestamp, SensorReadingResource auto-generates them
 * server-side. UUID is used for id to guarantee global uniqueness without a
 * central sequence generator.
 */

public class SensorReading {
    private String id;
    private long timestamp; // when the reading was taken (epoch ms)
    private double value;

    public SensorReading() {}
    public SensorReading(String id, long timestamp, double value) {
        this.id = id; this.timestamp = timestamp; this.value = value;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
