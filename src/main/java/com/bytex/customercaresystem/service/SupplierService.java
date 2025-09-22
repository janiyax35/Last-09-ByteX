package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierService {

    /**
     * Finds all suppliers.
     * @return A list of all suppliers.
     */
    List<Supplier> findAll();

    Supplier save(Supplier supplier);

    void deleteById(Long id);

    Optional<Supplier> findById(Long id);

    List<Supplier> findByParts(com.bytex.customercaresystem.model.Part part);
}
