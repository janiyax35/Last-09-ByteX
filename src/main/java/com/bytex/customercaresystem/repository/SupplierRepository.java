package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    java.util.List<Supplier> findByParts(com.bytex.customercaresystem.model.Part part);
}
