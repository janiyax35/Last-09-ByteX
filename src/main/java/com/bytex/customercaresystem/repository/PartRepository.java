package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    /**
     * Finds all parts whose status is in the given list of statuses.
     * @param statuses A list of PartStatus enums.
     * @return A list of matching parts.
     */
    java.util.List<Part> findByStatusIn(java.util.List<com.bytex.customercaresystem.model.PartStatus> statuses);
}
