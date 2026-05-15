package com.epharmacy.services;

import com.epharmacy.dao.PrescriptionDAO;
import com.epharmacy.models.Prescription;

import java.sql.SQLException;
import java.util.List;

/** Service for prescription upload and approval workflow. */
public class PrescriptionService {

    private final PrescriptionDAO dao = new PrescriptionDAO();

    public void upload(Prescription p) {
        try { dao.save(p); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void approve(int id, String notes) {
        try { dao.updateStatus(id, Prescription.Status.APPROVED, notes); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void reject(int id, String notes) {
        try { dao.updateStatus(id, Prescription.Status.REJECTED, notes); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Prescription> getPending() {
        try { return dao.findPending(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Prescription> getAll() {
        try { return dao.findAll(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Prescription> getByPatient(int patientId) {
        try { return dao.findByPatient(patientId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}
