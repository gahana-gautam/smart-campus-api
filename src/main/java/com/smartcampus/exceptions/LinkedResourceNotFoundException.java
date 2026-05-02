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
 * Thrown when a sensor is registered with a roomId that does not exist.
 *
 * The request body is syntactically valid JSON but contains a reference
 * to a non-existent resource, making it semantically unable to process.
 *
 * Mapped to HTTP 422 Unable to process Entity by LinkedResourceNotFoundExceptionMapper.
 * 422 is more precise than 404 here because the endpoint was found successfully 
 * the problem is a broken reference inside the payload, not a missing URI.
 */
public class LinkedResourceNotFoundException extends RuntimeException{
     public LinkedResourceNotFoundException(String message) { super(message); }
}
