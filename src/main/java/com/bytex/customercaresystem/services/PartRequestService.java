package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.PartRequest;
import com.bytex.customercaresystem.repositories.PartRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PartRequestService {

    @Autowired
    private PartRequestRepository partRequestRepository;

    @Transactional
    public PartRequest createPartRequest(PartRequest partRequest) {
        return partRequestRepository.save(partRequest);
    }

    public List<PartRequest> findAll() {
        return partRequestRepository.findAll();
    }

    public Optional<PartRequest> findById(Long id) {
        return partRequestRepository.findById(id);
    }

    @Transactional
    public PartRequest save(PartRequest partRequest) {
        return partRequestRepository.save(partRequest);
    }
}