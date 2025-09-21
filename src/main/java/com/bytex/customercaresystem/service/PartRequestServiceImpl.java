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

    public PartRequestServiceImpl(PartRequestRepository partRequestRepository, com.bytex.customercaresystem.repository.PartRepository partRepository) {
        this.partRequestRepository = partRequestRepository;
        this.partRepository = partRepository;
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
        // Explicitly save the updated part to persist stock changes
        partRepository.save(part);

        request.setStatus(PartRequestStatus.FULFILLED); // More descriptive status
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
}
