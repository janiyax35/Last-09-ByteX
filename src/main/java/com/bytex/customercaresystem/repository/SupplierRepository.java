package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByParts(Part part);
}
