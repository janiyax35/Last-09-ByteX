package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface PartRequestService {
    PartRequest createPartRequest(User requestor, Part part, int quantity, String reason, Repair repair);
    List<PartRequest> findPendingRequests();
    PartRequest approveRequest(Long requestId) throws Exception;
    PartRequest rejectRequest(Long requestId) throws Exception;
    PartRequest forwardRequestToWarehouse(Long requestId) throws Exception;
    List<PartRequest> findWarehousePendingRequests();
    Optional<PartRequest> findById(Long id);
    PartRequest fulfillRequestFromWarehouse(Long requestId) throws Exception;
}
