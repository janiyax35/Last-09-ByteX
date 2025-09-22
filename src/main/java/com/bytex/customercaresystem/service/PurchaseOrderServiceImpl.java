package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.*;
import com.bytex.customercaresystem.repository.PartRepository;
import com.bytex.customercaresystem.repository.PartRequestRepository;
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
    private final PartRequestRepository partRequestRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PartRepository partRepository, PartRequestRepository partRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.partRepository = partRepository;
        this.partRequestRepository = partRequestRepository;
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
        return purchaseOrderRepository.findAllWithDetails();
    }

    @Override
    @Transactional
    public PurchaseOrder updateStatus(Long poId, PurchaseOrderStatus status) throws Exception {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new Exception("Purchase Order not found."));
        po.setStatus(status);
        if (status == PurchaseOrderStatus.DELIVERED) {
            po.setActualDelivery(LocalDateTime.now());
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
    @Transactional
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
        orderItem.setPurchaseOrder(po);
        orderItem.setId(new OrderItemId(poId, orderItem.getPart().getPartId()));
        po.getOrderItems().add(orderItem);
        po.setTotalAmount(po.getTotalAmount().add(orderItem.getUnitPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity()))));
        return purchaseOrderRepository.save(po);
    }

    @Override
    @Transactional
    public PurchaseOrder createPoFromRequest(Long partRequestId, PurchaseOrder purchaseOrder, OrderItem orderItem, User creator) throws Exception {
        PartRequest partRequest = partRequestRepository.findById(partRequestId)
                .orElseThrow(() -> new Exception("Part Request not found."));

        purchaseOrder.setCreatedBy(creator);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
        purchaseOrder.setTotalAmount(orderItem.getUnitPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity())));
        PurchaseOrder savedPo = purchaseOrderRepository.save(purchaseOrder);

        orderItem.setPurchaseOrder(savedPo);
        orderItem.setId(new OrderItemId(savedPo.getOrderId(), orderItem.getPart().getPartId()));
        savedPo.getOrderItems().add(orderItem);

        partRequest.setStatus(PartRequestStatus.PURCHASE_ORDERED);
        if (partRequest.getRepair() != null && partRequest.getRepair().getTicket() != null) {
            partRequest.getRepair().getTicket().setStage(TicketStage.SUPPLIER_ORDERED);
        }
        partRequestRepository.save(partRequest);

        return purchaseOrderRepository.save(savedPo);
    }
}
