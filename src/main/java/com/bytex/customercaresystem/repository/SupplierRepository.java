package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    // Basic CRUD methods are inherited from JpaRepository
}
