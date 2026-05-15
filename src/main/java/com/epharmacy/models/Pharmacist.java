package com.epharmacy.models;

/** Pharmacist user — manages stock, approves prescriptions, handles orders. */
public class Pharmacist extends User {

    public Pharmacist() { setRole(Role.PHARMACIST); }

    public Pharmacist(String name, String email, String password) {
        super(name, email, password, Role.PHARMACIST);
    }
}
