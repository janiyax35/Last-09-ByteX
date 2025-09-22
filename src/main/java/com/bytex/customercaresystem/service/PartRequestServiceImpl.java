package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.PartRequestStatus;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.PartRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class PartRequestServiceImpl implements PartRequestService {

    private final PartRequestRepository partRequestRepository;
    private final com.bytex.customercaresystem.repository.PartRepository partRepository;
    private final com.bytex.customercaresystem.repository.TicketRepository ticketRepository;

    public PartRequestServiceImpl(PartRequestRepository partRequestRepository, com.bytex.customercaresystem.repository.PartRepository partRepository, com.bytex.customercaresystem.repository.TicketRepository ticketRepository) {
        this.partRequestRepository = partRequestRepository;
        this.partRepository = partRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public PartRequest createPartRequest(User requestor, Part part, int quantity, String reason, com.bytex.customercaresystem.model.Repair repair) {
        PartRequest newRequest = new PartRequest();
        newRequest.setRequestor(requestor);
        newRequest.setPart(part);
        newRequest.setQuantity(quantity);
        newRequest.setReason(reason);
        newRequest.setStatus(PartRequestStatus.PENDING);
        newRequest.setRepair(repair);
        return partRequestRepository.save(newRequest);
    }

    @Override
    public java.util.List<PartRequest> findPendingRequests() {
        return partRequestRepository.findByStatus(PartRequestStatus.PENDING);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public PartRequest approveRequest(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));

        Part part = request.getPart();
        if (part.getCurrentStock() < request.getQuantity()) {
            throw new Exception("Insufficient stock for part: " + part.getPartName());
        }

        // Deduct stock
        part.setCurrentStock(part.getCurrentStock() - request.getQuantity());

        // Update part status if necessary
        if (part.getCurrentStock() == 0) {
            part.setStatus(com.bytex.customercaresystem.model.PartStatus.OUT_OF_STOCK);
        } else if (part.getCurrentStock() < part.getMinimumStock()) {
            part.setStatus(com.bytex.customercaresystem.model.PartStatus.LOW_STOCK);
        }
        partRepository.save(part);

        // Update ticket stage to notify technician
        com.bytex.customercaresystem.model.Repair repair = request.getRepair();
        if(repair != null) {
            com.bytex.customercaresystem.model.Ticket ticket = repair.getTicket();
            if (ticket != null) {
                ticket.setStage(com.bytex.customercaresystem.model.TicketStage.WITH_TECHNICIAN); // Or a new 'PARTS_DELIVERED' stage
                ticketRepository.save(ticket);
            }
        }

        request.setStatus(PartRequestStatus.FULFILLED);
        request.setFulfillmentDate(java.time.LocalDateTime.now());
        return partRequestRepository.save(request);
    }

    @Override
    public PartRequest rejectRequest(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));
        request.setStatus(PartRequestStatus.REJECTED);
        return partRequestRepository.save(request);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public PartRequest forwardRequestToWarehouse(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));

        // Update the ticket stage
        com.bytex.customercaresystem.model.Repair repair = request.getRepair();
        if (repair != null) {
            com.bytex.customercaresystem.model.Ticket ticket = repair.getTicket();
            if (ticket != null) {
                ticket.setStage(com.bytex.customercaresystem.model.TicketStage.WAREHOUSE_REQUESTED);
                ticketRepository.save(ticket);
            }
        }

        request.setStatus(PartRequestStatus.PENDING_WAREHOUSE);
        return partRequestRepository.save(request);
    }

    @Override
    public java.util.List<PartRequest> findWarehousePendingRequests() {
        return partRequestRepository.findByStatusWithDetails(PartRequestStatus.PENDING_WAREHOUSE);
    }

    @Override
    public java.util.Optional<PartRequest> findById(Long id) {
        return partRequestRepository.findById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public PartRequest fulfillRequestFromWarehouse(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));

        Part part = request.getPart();
        if (part.getCurrentStock() < request.getQuantity()) {
            throw new Exception("Insufficient stock for part: " + part.getPartName());
        }

        // Deduct stock
        part.setCurrentStock(part.getCurrentStock() - request.getQuantity());

        // Update part status if necessary
        if (part.getCurrentStock() == 0) {
            part.setStatus(com.bytex.customercaresystem.model.PartStatus.OUT_OF_STOCK);
        } else if (part.getCurrentStock() < part.getMinimumStock()) {
            part.setStatus(com.bytex.customercaresystem.model.PartStatus.LOW_STOCK);
        }
        partRepository.save(part);

        // Update ticket stage to notify technician
        com.bytex.customercaresystem.model.Repair repair = request.getRepair();
        if(repair != null) {
            com.bytex.customercaresystem.model.Ticket ticket = repair.getTicket();
            if (ticket != null) {
                ticket.setStage(com.bytex.customercaresystem.model.TicketStage.WITH_TECHNICIAN);
                ticketRepository.save(ticket);
            }
        }

        request.setStatus(PartRequestStatus.FULFILLED);
        request.setFulfillmentDate(java.time.LocalDateTime.now());
        return partRequestRepository.save(request);
    }
}
