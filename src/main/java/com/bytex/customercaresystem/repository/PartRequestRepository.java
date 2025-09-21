package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PartRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {

    /**
     * Finds all part requests with a specific status.
     * @param status The status to search for.
     * @return A list of part requests.
     */
    java.util.List<PartRequest> findByStatus(com.bytex.customercaresystem.model.PartRequestStatus status);
}
