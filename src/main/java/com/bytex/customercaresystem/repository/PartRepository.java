package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByStatusIn(List<PartStatus> statuses);
}
