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
    private final com.bytex.customercaresystem.repository.PartRequestRepository partRequestRepository;
    private final com.bytex.customercaresystem.repository.TicketRepository ticketRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PartRepository partRepository, com.bytex.customercaresystem.repository.PartRequestRepository partRequestRepository, com.bytex.customercaresystem.repository.TicketRepository ticketRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.partRepository = partRepository;
        this.partRequestRepository = partRequestRepository;
        this.ticketRepository = ticketRepository;
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

    @Override
    @Transactional
    public PurchaseOrder createPoFromRequest(Long partRequestId, PurchaseOrder purchaseOrder, OrderItem orderItem, User creator) throws Exception {
        // 1. Find the original PartRequest
        PartRequest partRequest = partRequestRepository.findById(partRequestId)
                .orElseThrow(() -> new Exception("Part Request not found."));

        // 2. Create and save the new PurchaseOrder
        purchaseOrder.setCreatedBy(creator);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);

        // Calculate total amount from the single item
        java.math.BigDecimal itemTotal = orderItem.getUnitPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity()));
        purchaseOrder.setTotalAmount(itemTotal);

        PurchaseOrder savedPo = purchaseOrderRepository.save(purchaseOrder);

        // 3. Create and save the OrderItem
        orderItem.setPurchaseOrder(savedPo);
        OrderItemId orderItemId = new OrderItemId(savedPo.getOrderId(), orderItem.getPart().getPartId());
        orderItem.setId(orderItemId);
        savedPo.getOrderItems().add(orderItem); // Add to the collection

        // 4. Update the PartRequest status
        partRequest.setStatus(PartRequestStatus.PURCHASE_ORDERED);
        partRequestRepository.save(partRequest);

        // 5. Update the Ticket stage
        if (partRequest.getRepair() != null && partRequest.getRepair().getTicket() != null) {
            Ticket ticket = partRequest.getRepair().getTicket();
            ticket.setStage(TicketStage.SUPPLIER_ORDERED);
            ticketRepository.save(ticket);
        }

        return purchaseOrderRepository.save(savedPo);
    }
}
