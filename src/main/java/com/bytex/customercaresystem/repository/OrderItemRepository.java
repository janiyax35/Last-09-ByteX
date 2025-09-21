package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.OrderItem;
import com.bytex.customercaresystem.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    // Basic CRUD methods are inherited from JpaRepository
}
