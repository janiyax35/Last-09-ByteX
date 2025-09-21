package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    // Basic CRUD methods are inherited from JpaRepository
}
