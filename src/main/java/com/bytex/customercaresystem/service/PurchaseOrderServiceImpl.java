package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.*;
import com.bytex.customercaresystem.repository.PartRepository;
import com.bytex.customercaresystem.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PartRepository partRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PartRepository partRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.partRepository = partRepository;
    }

    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public Optional<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional
    public PurchaseOrder updateStatus(Long poId, PurchaseOrderStatus status) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new Exception("Purchase Order not found."));

        po.setStatus(status);

        if (status == PurchaseOrderStatus.DELIVERED) {
            po.setActualDelivery(LocalDateTime.now());
            // Increase stock for each item in the order
            for (OrderItem item : po.getOrderItems()) {
                Part part = item.getPart();
                int newStock = part.getCurrentStock() + item.getQuantity();
                part.setCurrentStock(newStock);
                partRepository.save(part);
            }
        }

        return purchaseOrderRepository.save(po);
    }

    @Override
    public void cancelOrder(Long poId) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new Exception("Purchase Order not found."));

        if (po.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new Exception("Only pending orders can be canceled.");
        }
        po.setStatus(PurchaseOrderStatus.CANCELLED);
        purchaseOrderRepository.save(po);
    }

    @Override
    @Transactional
    public PurchaseOrder addOrderItem(Long poId, OrderItem orderItem) throws Exception {
        PurchaseOrder po = findById(poId).orElseThrow(() -> new Exception("Purchase Order not found."));

        // Set the bidirectional relationship
        orderItem.setPurchaseOrder(po);

        // This assumes the OrderItemId is partially filled from the form
        OrderItemId id = new OrderItemId(poId, orderItem.getPart().getPartId());
        orderItem.setId(id);

        po.getOrderItems().add(orderItem);

        // Update the total amount
        java.math.BigDecimal itemTotal = orderItem.getUnitPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity()));
        po.setTotalAmount(po.getTotalAmount().add(itemTotal));

        return purchaseOrderRepository.save(po);
    }
}
