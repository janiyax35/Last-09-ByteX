package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByTechnician(User technician);
}
