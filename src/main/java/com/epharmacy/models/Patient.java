package com.epharmacy.models;

/** Patient user — can browse, order medicines, and upload prescriptions. */
public class Patient extends User {

    public Patient() { setRole(Role.PATIENT); }

    public Patient(String name, String email, String password) {
        super(name, email, password, Role.PATIENT);
    }
}
