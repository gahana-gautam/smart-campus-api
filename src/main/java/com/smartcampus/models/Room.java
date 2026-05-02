/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.models;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Asus
 */
/**
 * Domain model representing a physical room on the Smart Campus.
 *
 * A Room is the top-level aggregate in the resource hierarchy.
 * Sensors are always owned by exactly one room, forming a one-to-many
 * relationship reflected both in this class (via sensorIds) and in the
 * nested URI structure /rooms/{roomId}.
 *
 * The sensorIds list acts as a lightweight foreign-key index allowing the API
 * to quickly determine whether a room is occupied without scanning the entire
 * sensor collection critical for the deletion safety check in RoomResource.
 */
public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>(); 

    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}

