package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.PartRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
    @Query("SELECT pr FROM PartRequest pr JOIN FETCH pr.part JOIN FETCH pr.requestor WHERE pr.status = :status")
    List<PartRequest> findByStatusWithDetails(@Param("status") PartRequestStatus status);
}
