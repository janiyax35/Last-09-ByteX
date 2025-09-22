package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.OrderItem;
import com.bytex.customercaresystem.model.PurchaseOrder;
import com.bytex.customercaresystem.model.PurchaseOrderStatus;
import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    PurchaseOrder save(PurchaseOrder purchaseOrder);
    Optional<PurchaseOrder> findById(Long id);
    List<PurchaseOrder> findAll();
    PurchaseOrder updateStatus(Long poId, PurchaseOrderStatus status) throws Exception;
    void cancelOrder(Long poId) throws Exception;
    PurchaseOrder addOrderItem(Long poId, OrderItem orderItem) throws Exception;
    PurchaseOrder createPoFromRequest(Long partRequestId, PurchaseOrder purchaseOrder, OrderItem orderItem, User creator) throws Exception;
}
