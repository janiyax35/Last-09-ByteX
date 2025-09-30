package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.Part;
import com.bytex.customercaresystem.repositories.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    public List<Part> findAllParts() {
        return partRepository.findAll();
    }

    public Optional<Part> findPartById(Long id) {
        return partRepository.findById(id);
    }

    @Transactional
    public Part savePart(Part part) {
        return partRepository.save(part);
    }

    @Transactional
    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }
}