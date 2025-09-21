package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

public interface RepairService {

    /**
     * Creates a new repair record when a ticket is escalated to a technician.
     * @param ticket The ticket to be repaired.
     * @param technician The technician assigned to the repair.
     * @param diagnosis The initial diagnosis from the staff member.
     * @return The newly created Repair object.
     */
    Repair createRepair(Ticket ticket, User technician, String diagnosis);

    /**
     * Finds all repairs assigned to a specific technician.
     * @param technician The technician user.
     * @return A list of the technician's repairs.
     */
    java.util.List<Repair> findRepairsByTechnician(User technician);

    /**
     * Finds a repair by its ID.
     * @param id The ID of the repair.
     * @return An Optional containing the repair if found.
     */
    java.util.Optional<Repair> findById(Long id);

    /**
     * Updates the status of a repair.
     * @param repair The repair to update.
     * @param status The new status.
     * @return The updated repair.
     */
    Repair updateRepairStatus(Repair repair, com.bytex.customercaresystem.model.RepairStatus status);

    /**
     * Adds details/notes to a repair record.
     * @param repair The repair to update.
     * @param details The details to add.
     * @return The updated repair.
     */
    Repair addRepairDetails(Repair repair, String details);
}
