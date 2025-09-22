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

    /**
     * Finds all part requests with a specific status, eagerly fetching related entities to prevent LazyInitializationException.
     * @param status The status to search for.
     * @return A list of part requests with their part and requestor details loaded.
     */
    @org.springframework.data.jpa.repository.Query("SELECT pr FROM PartRequest pr JOIN FETCH pr.part JOIN FETCH pr.requestor WHERE pr.status = :status")
    java.util.List<PartRequest> findByStatusWithDetails(@org.springframework.data.repository.query.Param("status") com.bytex.customercaresystem.model.PartRequestStatus status);
}
