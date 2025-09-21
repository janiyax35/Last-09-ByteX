package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {

    /**
     * Finds all repairs assigned to a specific technician.
     * @param technician The technician user.
     * @return A list of repairs.
     */
    java.util.List<Repair> findByTechnician(com.bytex.customercaresystem.model.User technician);
}
