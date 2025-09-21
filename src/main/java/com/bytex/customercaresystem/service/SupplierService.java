package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Supplier;
import java.util.List;

public interface SupplierService {

    /**
     * Finds all suppliers.
     * @return A list of all suppliers.
     */
    List<Supplier> findAll();

    Supplier save(Supplier supplier);

    void deleteById(Long id);

    Optional<Supplier> findById(Long id);

}
