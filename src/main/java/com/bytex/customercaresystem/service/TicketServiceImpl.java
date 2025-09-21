package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.TicketStatus;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findTicketsByCustomer(User customer) {
        return ticketRepository.findByCustomer(customer);
    }

    @Override
    public Ticket createTicket(Ticket ticket, User customer) {
        ticket.setCustomer(customer);
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    @Override
    public void cancelTicket(Long ticketId, User customer) throws Exception {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new Exception("Ticket not found."));

        if (!ticket.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new Exception("You are not authorized to cancel this ticket.");
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new Exception("This ticket cannot be canceled as it is already being processed.");
        }

        ticketRepository.delete(ticket);
    }

    @Override
    public List<Ticket> findTicketsByAssignedTo(User staffMember) {
        return ticketRepository.findByAssignedTo(staffMember);
    }

    @Override
    public List<Ticket> findUnassignedTickets() {
        return ticketRepository.findByAssignedToIsNull();
    }

    @Override
    public Ticket acceptTicket(Ticket ticket, User staffMember) {
        ticket.setAssignedTo(staffMember);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicketStatus(Ticket ticket, TicketStatus status) {
        ticket.setStatus(status);
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            ticket.setClosedAt(java.time.LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket archiveTicket(Ticket ticket) {
        ticket.setArchived(true);
        ticket.setArchivedAt(java.time.LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
}
