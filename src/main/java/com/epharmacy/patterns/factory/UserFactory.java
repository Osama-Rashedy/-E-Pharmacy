package com.epharmacy.patterns.factory;

import com.epharmacy.models.*;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Factory Method (Creational)     ║
 * ║  Centralises creation of User subtypes so        ║
 * ║  callers never use 'new Admin()', etc. directly. ║
 * ╚══════════════════════════════════════════════════╝
 */
public class UserFactory {

    /**
     * Creates the correct User subtype based on the role string.
     *
     * @param role     "ADMIN" | "PHARMACIST" | "PATIENT"
     * @param name     display name
     * @param email    login email
     * @param password plain-text password (caller should hash before saving)
     */
    public static User createUser(String role, String name, String email, String password) {
        return switch (role.toUpperCase()) {
            case "ADMIN"      -> new Admin(name, email, password);
            case "PHARMACIST" -> new Pharmacist(name, email, password);
            case "PATIENT"    -> new Patient(name, email, password);
            default           -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    /** Overload that accepts the enum directly. */
    public static User createUser(User.Role role, String name, String email, String password) {
        return createUser(role.name(), name, email, password);
    }
}
