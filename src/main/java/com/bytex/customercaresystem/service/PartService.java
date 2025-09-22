package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import java.util.List;
import java.util.Optional;

public interface PartService {
    List<Part> findAll();
    Optional<Part> findById(Long id);
    Part save(Part part);
    List<Part> findLowStockParts();
    void discontinuePart(Long id);
}
