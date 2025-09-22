package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.RepairStatus;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.TicketStatus;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.RepairRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;

    public RepairServiceImpl(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    @Override
    @Transactional
    public Repair createRepair(Ticket ticket, User technician, String diagnosis) {
        Repair newRepair = new Repair();
        newRepair.setTicket(ticket);
        newRepair.setTechnician(technician);
        newRepair.setDiagnosis(diagnosis);
        newRepair.setStatus(RepairStatus.PENDING);
        return repairRepository.save(newRepair);
    }

    @Override
    public List<Repair> findRepairsByTechnician(User technician) {
        return repairRepository.findByTechnician(technician);
    }

    @Override
    public Optional<Repair> findById(Long id) {
        return repairRepository.findById(id);
    }

    @Override
    @Transactional
    public Repair updateRepairStatus(Repair repair, RepairStatus status) {
        repair.setStatus(status);
        if (status == RepairStatus.COMPLETED || status == RepairStatus.FAILED) {
            repair.setCompletionDate(LocalDateTime.now());
            if (repair.getTicket() != null) {
                repair.getTicket().setStatus(TicketStatus.RESOLVED);
            }
        }
        return repairRepository.save(repair);
    }

    @Override
    @Transactional
    public Repair addRepairDetails(Repair repair, String details) {
        String existingDetails = repair.getRepairDetails() == null ? "" : repair.getRepairDetails();
        String newDetails = existingDetails + "\n--- Update [" + LocalDateTime.now() + "] ---\n" + details;
        repair.setRepairDetails(newDetails);
        return repairRepository.save(repair);
    }
}
