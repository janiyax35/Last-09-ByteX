package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.createdBy LEFT JOIN FETCH po.supplier ORDER BY po.orderDate DESC")
    java.util.List<PurchaseOrder> findAllWithDetails();
}
