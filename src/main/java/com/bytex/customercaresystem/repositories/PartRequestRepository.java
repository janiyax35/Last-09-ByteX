package com.bytex.customercaresystem.repositories;

import com.bytex.customercaresystem.models.PartRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
}