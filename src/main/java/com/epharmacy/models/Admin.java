package com.epharmacy.models;

/** Admin user — has unrestricted access to all system features. */
public class Admin extends User {

    public Admin() { setRole(Role.ADMIN); }

    public Admin(String name, String email, String password) {
        super(name, email, password, Role.ADMIN);
    }
}
