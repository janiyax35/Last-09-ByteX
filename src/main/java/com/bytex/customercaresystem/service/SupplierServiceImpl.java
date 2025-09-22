package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.Supplier;
import com.bytex.customercaresystem.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public void deleteById(Long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> findByParts(Part part) {
        return supplierRepository.findByParts(part);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Supplier supplierWithUpdates) throws Exception {
        Supplier existingSupplier = supplierRepository.findById(supplierWithUpdates.getSupplierId())
                .orElseThrow(() -> new Exception("Supplier not found with id: " + supplierWithUpdates.getSupplierId()));

        existingSupplier.setSupplierName(supplierWithUpdates.getSupplierName());
        existingSupplier.setContactInfo(supplierWithUpdates.getContactInfo());
        existingSupplier.setAddress(supplierWithUpdates.getAddress());
        existingSupplier.setParts(supplierWithUpdates.getParts());

        return supplierRepository.save(existingSupplier);
    }
}
