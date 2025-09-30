package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.repositories.TicketRepository;
import com.bytex.customercaresystem.specifications.TicketSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional
    public Ticket createTicket(Ticket ticket, User customer) {
        ticket.setCustomer(customer);
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> findTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findTicketsByCustomer(User customer) {
        return ticketRepository.findByCustomer(customer);
    }

    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> searchTickets(String keyword, String status) {
        Specification<Ticket> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and(TicketSpecification.hasKeyword(keyword));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(TicketSpecification.hasStatus(status));
        }
        return ticketRepository.findAll(spec);
    }
}