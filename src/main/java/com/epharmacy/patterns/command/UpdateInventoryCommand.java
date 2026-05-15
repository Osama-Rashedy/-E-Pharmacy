package com.epharmacy.patterns.command;

import com.epharmacy.models.Medicine;
import com.epharmacy.services.MedicineService;

/** Command: Update medicine stock quantity. */
public class UpdateInventoryCommand implements OrderCommand {

    private final MedicineService medicineService;
    private final int             medicineId;
    private final int             newQuantity;
    private int                   previousQuantity;

    public UpdateInventoryCommand(MedicineService medicineService, int medicineId, int newQuantity) {
        this.medicineService = medicineService;
        this.medicineId      = medicineId;
        this.newQuantity     = newQuantity;
    }

    @Override
    public void execute() {
        Medicine med = medicineService.findByIdOrNull(medicineId);
        if (med != null) {
            previousQuantity = med.getQuantity();
            med.setQuantity(newQuantity);
            medicineService.updateMedicine(med);
            System.out.println("[Command] Medicine #" + medicineId + " stock updated to " + newQuantity);
        }
    }

    @Override
    public void undo() {
        Medicine med = medicineService.findByIdOrNull(medicineId);
        if (med != null) {
            med.setQuantity(previousQuantity);
            medicineService.updateMedicine(med);
            System.out.println("[Command] Undo stock update → Medicine #" + medicineId + " restored to " + previousQuantity);
        }
    }
}
