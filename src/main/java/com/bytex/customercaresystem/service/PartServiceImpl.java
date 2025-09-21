package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    public PartServiceImpl(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public List<Part> findAll() {
        return partRepository.findAll();
    }

    @Override
    public Optional<Part> findById(Long id) {
        return partRepository.findById(id);
    }

    @Override
    public Part save(Part part) {
        return partRepository.save(part);
    }

    @Override
    public List<Part> findLowStockParts() {
        return partRepository.findByStatusIn(java.util.List.of(com.bytex.customercaresystem.model.PartStatus.LOW_STOCK, com.bytex.customercaresystem.model.PartStatus.OUT_OF_STOCK));
    }

    @Override
    public void discontinuePart(Long id) {
        Part part = findById(id).orElseThrow(() -> new RuntimeException("Part not found with id: " + id));
        part.setStatus(com.bytex.customercaresystem.model.PartStatus.DISCONTINUED);
        partRepository.save(part);
    }
}
