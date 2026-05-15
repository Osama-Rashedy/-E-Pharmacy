package com.epharmacy.models;

/**
 * Base entity for all system users (Admin, Pharmacist, Patient).
 */
public class User {

    public enum Role { ADMIN, PHARMACIST, PATIENT }

    private int    id;
    private String name;
    private String email;
    private String password;  // BCrypt hash
    private Role   role;
    private String phone;
    private String address;

    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Getters & Setters ───────────────────────────────────────────
    public int    getId()                   { return id; }
    public void   setId(int id)             { this.id = id; }

    public String getName()                 { return name; }
    public void   setName(String name)      { this.name = name; }

    public String getEmail()                { return email; }
    public void   setEmail(String email)    { this.email = email; }

    public String getPassword()             { return password; }
    public void   setPassword(String pw)    { this.password = pw; }

    public Role   getRole()                 { return role; }
    public void   setRole(Role role)        { this.role = role; }

    public String getPhone()                { return phone; }
    public void   setPhone(String phone)    { this.phone = phone; }

    public String getAddress()              { return address; }
    public void   setAddress(String addr)   { this.address = addr; }

    @Override public String toString()      { return name + " <" + email + ">"; }
}
