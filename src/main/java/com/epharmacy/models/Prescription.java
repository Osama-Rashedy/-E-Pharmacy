package com.epharmacy.models;

import java.time.LocalDateTime;

/** Prescription uploaded by a patient. */
public class Prescription {

    public enum Status { PENDING, APPROVED, REJECTED }

    private int           id;
    private int           patientId;
    private String        patientName;  // display only
    private Integer       orderId;      // nullable
    private String        imagePath;
    private Status        status;
    private String        notes;
    private LocalDateTime uploadedAt;

    public Prescription() {}

    // ── Getters & Setters ───────────────────────────────────────────
    public int       getId()                         { return id; }
    public void      setId(int id)                   { this.id = id; }

    public int       getPatientId()                  { return patientId; }
    public void      setPatientId(int pid)           { this.patientId = pid; }

    public String    getPatientName()                { return patientName; }
    public void      setPatientName(String name)     { this.patientName = name; }

    public Integer   getOrderId()                    { return orderId; }
    public void      setOrderId(Integer oid)         { this.orderId = oid; }

    public String    getImagePath()                  { return imagePath; }
    public void      setImagePath(String path)       { this.imagePath = path; }

    public Status    getStatus()                     { return status; }
    public void      setStatus(Status status)        { this.status = status; }

    public String    getNotes()                      { return notes; }
    public void      setNotes(String notes)          { this.notes = notes; }

    public LocalDateTime getUploadedAt()             { return uploadedAt; }
    public void          setUploadedAt(LocalDateTime dt) { this.uploadedAt = dt; }
}
