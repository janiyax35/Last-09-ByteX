package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    // Basic CRUD methods are inherited from JpaRepository
}
