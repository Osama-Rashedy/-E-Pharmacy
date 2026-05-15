package com.epharmacy.services;

import com.epharmacy.dao.MedicineDAO;
import com.epharmacy.models.Medicine;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for medicine management.
 * The MedicineServiceProxy wraps this class for role-based access control.
 */
public class MedicineService {

    private final MedicineDAO dao = new MedicineDAO();

    public List<Medicine> getAllMedicines() {
        try { return dao.findAll(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Optional<Medicine> findById(int id) {
        try { return dao.findById(id); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** findById convenience — returns null if not found. */
    public Medicine findByIdOrNull(int id) {
        return findById(id).orElse(null);
    }

    public void addMedicine(Medicine m) {
        try { dao.save(m); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void updateMedicine(Medicine m) {
        try { dao.update(m); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void deleteMedicine(int id) {
        try { dao.delete(id); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Medicine> search(String keyword) {
        try { return dao.search(keyword); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Medicine> getLowStockMedicines() {
        try { return dao.findLowStock(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Medicine> getExpiringSoon() {
        try { return dao.findExpiringSoon(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getTotalCount() {
        try { return dao.count(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}
