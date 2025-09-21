package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.RepairPart;
import com.bytex.customercaresystem.model.RepairPartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairPartRepository extends JpaRepository<RepairPart, RepairPartId> {
    // Basic CRUD methods are inherited from JpaRepository
}
