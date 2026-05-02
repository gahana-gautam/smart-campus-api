/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

import javax.ws.rs.core.Application;
/**
 *
 * @author Asus
 */
/**
 * JAX-RS application descriptor for the Smart Campus API.
 *
 * In a traditional servlet-container deployment, annotating this class with
 * @ApplicationPath("/api/v1") would establish the versioned base path.
 * However, because this project uses an embedded Grizzly server bootstrapped
 * from Main.java, the annotation is intentionally omitted to prevent a second
 * application context being registered at the same path, which would cause
 * HTTP 400 errors on every request.
 *
 * All registration and base URI configuration are handled in Main.java.
 */
// @ApplicationPath("/api/v1") -- disabled, Main.java owns the base path
public class SmartCampusApplication extends Application {
    
}
