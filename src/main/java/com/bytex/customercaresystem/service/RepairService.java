package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.RepairStatus;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface RepairService {
    Repair createRepair(Ticket ticket, User technician, String diagnosis);
    List<Repair> findRepairsByTechnician(User technician);
    Optional<Repair> findById(Long id);
    Repair updateRepairStatus(Repair repair, RepairStatus status);
    Repair addRepairDetails(Repair repair, String details);
}
