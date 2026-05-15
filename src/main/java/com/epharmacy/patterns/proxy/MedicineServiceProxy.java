package com.epharmacy.patterns.proxy;

import com.epharmacy.models.Medicine;
import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.MedicineService;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Proxy (Structural)              ║
 * ║  Wraps MedicineService and enforces role-based   ║
 * ║  access control before any destructive action.   ║
 * ╚══════════════════════════════════════════════════╝
 */
public class MedicineServiceProxy {

    private final MedicineService realService;

    public MedicineServiceProxy(MedicineService realService) {
        this.realService = realService;
    }

    // ── Public read operations (any role allowed) ──────────────────

    public List<Medicine> getAllMedicines() {
        return realService.getAllMedicines();
    }

    public List<Medicine> search(String keyword) {
        return realService.search(keyword);
    }

    // ── Write operations (Admin or Pharmacist only) ────────────────

    public void addMedicine(Medicine m) {
        checkWriteAccess();
        realService.addMedicine(m);
    }

    public void updateMedicine(Medicine m) {
        checkWriteAccess();
        realService.updateMedicine(m);
    }

    public void deleteMedicine(int id) {
        // Only Admin can delete medicines
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null || user.getRole() != User.Role.ADMIN) {
            throw new SecurityException("Only Admins can delete medicines.");
        }
        realService.deleteMedicine(id);
    }

    // ── Access guard ───────────────────────────────────────────────

    private void checkWriteAccess() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            throw new SecurityException("Not authenticated.");
        }
        if (user.getRole() == User.Role.PATIENT) {
            throw new SecurityException("Patients cannot modify medicines.");
        }
    }
}
