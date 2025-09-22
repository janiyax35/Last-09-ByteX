package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    List<Supplier> findAll();
    Supplier save(Supplier supplier);
    void deleteById(Long id);
    Optional<Supplier> findById(Long id);
    List<Supplier> findByParts(Part part);
    Supplier updateSupplier(Supplier supplier) throws Exception;
}
