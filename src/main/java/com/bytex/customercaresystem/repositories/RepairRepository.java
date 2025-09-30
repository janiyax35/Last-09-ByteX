package com.bytex.customercaresystem.repositories;

import com.bytex.customercaresystem.models.Repair;
import com.bytex.customercaresystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findByTechnician(User technician);
}