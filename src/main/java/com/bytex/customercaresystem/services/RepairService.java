package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.Repair;
import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.models.enums.RepairStatus;
import com.bytex.customercaresystem.repositories.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RepairService {

    @Autowired
    private RepairRepository repairRepository;

    @Transactional
    public Repair createRepair(Ticket ticket, User technician, String diagnosis) {
        Repair repair = new Repair();
        repair.setTicket(ticket);
        repair.setTechnician(technician);
        repair.setDiagnosis(diagnosis);
        repair.setStatus(RepairStatus.PENDING); // Set initial status
        return repairRepository.save(repair);
    }

    public Optional<Repair> findById(Long id) {
        return repairRepository.findById(id);
    }

    public List<Repair> findByTechnician(User technician) {
        return repairRepository.findByTechnician(technician);
    }

    @Transactional
    public Repair saveRepair(Repair repair) {
        return repairRepository.save(repair);
    }
}