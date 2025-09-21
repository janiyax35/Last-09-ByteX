package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.RepairStatus;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.RepairRepository;
import org.springframework.stereotype.Service;

@Service
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;

    public RepairServiceImpl(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    @Override
    public Repair createRepair(Ticket ticket, User technician, String diagnosis) {
        Repair newRepair = new Repair();
        newRepair.setTicket(ticket);
        newRepair.setTechnician(technician);
        newRepair.setDiagnosis(diagnosis);
        newRepair.setStatus(RepairStatus.PENDING);
        return repairRepository.save(newRepair);
    }

    @Override
    public java.util.List<Repair> findRepairsByTechnician(User technician) {
        return repairRepository.findByTechnician(technician);
    }

    @Override
    public java.util.Optional<Repair> findById(Long id) {
        return repairRepository.findById(id);
    }

    @Override
    public Repair updateRepairStatus(Repair repair, RepairStatus status) {
        repair.setStatus(status);
        if (status == RepairStatus.COMPLETED || status == RepairStatus.FAILED) {
            repair.setCompletionDate(java.time.LocalDateTime.now());
            // Optionally, update the main ticket status as well
            if (repair.getTicket() != null) {
                repair.getTicket().setStatus(com.bytex.customercaresystem.model.TicketStatus.RESOLVED);
            }
        }
        return repairRepository.save(repair);
    }

    @Override
    public Repair addRepairDetails(Repair repair, String details) {
        String existingDetails = repair.getRepairDetails() == null ? "" : repair.getRepairDetails();
        String newDetails = existingDetails + "\n--- Update [" + java.time.LocalDateTime.now() + "] ---\n" + details;
        repair.setRepairDetails(newDetails);
        return repairRepository.save(repair);
    }
}
