package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.*;
import com.bytex.customercaresystem.repository.PartRepository;
import com.bytex.customercaresystem.repository.PartRequestRepository;
import com.bytex.customercaresystem.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PartRequestServiceImpl implements PartRequestService {

    private final PartRequestRepository partRequestRepository;
    private final PartRepository partRepository;
    private final TicketRepository ticketRepository;

    public PartRequestServiceImpl(PartRequestRepository partRequestRepository, PartRepository partRepository, TicketRepository ticketRepository) {
        this.partRequestRepository = partRequestRepository;
        this.partRepository = partRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public PartRequest createPartRequest(User requestor, Part part, int quantity, String reason, Repair repair) {
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
    public List<PartRequest> findPendingRequests() {
        return partRequestRepository.findByStatusWithDetails(PartRequestStatus.PENDING);
    }

    @Override
    @Transactional
    public PartRequest approveRequest(Long requestId) throws Exception {
        return fulfillRequest(requestId);
    }

    @Override
    @Transactional
    public PartRequest fulfillRequestFromWarehouse(Long requestId) throws Exception {
        return fulfillRequest(requestId);
    }

    private PartRequest fulfillRequest(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));

        Part part = request.getPart();
        if (part.getCurrentStock() < request.getQuantity()) {
            throw new Exception("Insufficient stock for part: " + part.getPartName());
        }

        part.setCurrentStock(part.getCurrentStock() - request.getQuantity());

        if (part.getCurrentStock() == 0) {
            part.setStatus(PartStatus.OUT_OF_STOCK);
        } else if (part.getCurrentStock() < part.getMinimumStock()) {
            part.setStatus(PartStatus.LOW_STOCK);
        }
        partRepository.save(part);

        if(request.getRepair() != null && request.getRepair().getTicket() != null) {
            request.getRepair().getTicket().setStage(TicketStage.WITH_TECHNICIAN);
        }

        request.setStatus(PartRequestStatus.FULFILLED);
        request.setFulfillmentDate(LocalDateTime.now());
        return partRequestRepository.save(request);
    }

    @Override
    @Transactional
    public PartRequest rejectRequest(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));
        request.setStatus(PartRequestStatus.REJECTED);
        return partRequestRepository.save(request);
    }

    @Override
    @Transactional
    public PartRequest forwardRequestToWarehouse(Long requestId) throws Exception {
        PartRequest request = partRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Part request not found."));
        if(request.getRepair() != null && request.getRepair().getTicket() != null) {
            request.getRepair().getTicket().setStage(TicketStage.WAREHOUSE_REQUESTED);
        }
        request.setStatus(PartRequestStatus.PENDING_WAREHOUSE);
        return partRequestRepository.save(request);
    }

    @Override
    public List<PartRequest> findWarehousePendingRequests() {
        return partRequestRepository.findByStatusWithDetails(PartRequestStatus.PENDING_WAREHOUSE);
    }

    @Override
    public Optional<PartRequest> findById(Long id) {
        return partRequestRepository.findById(id);
    }
}
