package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT po FROM PurchaseOrder po JOIN FETCH po.createdBy JOIN FETCH po.supplier")
    @Override
    java.util.List<PurchaseOrder> findAll();
}
