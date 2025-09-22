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
    private final RepairService repairService;
    private final ActivityLogService activityLogService;

    public TicketServiceImpl(TicketRepository ticketRepository, RepairService repairService, ActivityLogService activityLogService) {
        this.ticketRepository = ticketRepository;
        this.repairService = repairService;
        this.activityLogService = activityLogService;
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
        ticket.setStage(com.bytex.customercaresystem.model.TicketStage.AWAITING_ACCEPTANCE);
        Ticket savedTicket = ticketRepository.save(ticket);
        activityLogService.saveLog(customer, "CREATE_TICKET", "Customer created ticket #" + savedTicket.getTicketId());
        return savedTicket;
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
        ticket.setStage(com.bytex.customercaresystem.model.TicketStage.WITH_STAFF);
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

    @Override
    public Ticket escalateTicket(Ticket ticket, User technician, String diagnosis) {
        // Create the repair record
        repairService.createRepair(ticket, technician, diagnosis);

        // Update the ticket stage
        ticket.setStage(com.bytex.customercaresystem.model.TicketStage.WITH_TECHNICIAN);
        Ticket savedTicket = ticketRepository.save(ticket);

        // For logging, we need to know who escalated it. The service doesn't know.
        // This should be passed in from the controller. For now, we assume the assigned staff did it.
        if (ticket.getAssignedTo() != null) {
            activityLogService.saveLog(ticket.getAssignedTo(), "ESCALATE_TICKET", "Staff escalated ticket #" + savedTicket.getTicketId() + " to technician " + technician.getUsername());
        }

        return savedTicket;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Ticket updateTicketStage(Long ticketId, com.bytex.customercaresystem.model.TicketStage stage) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + ticketId));
        ticket.setStage(stage);
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> searchTickets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllTickets();
        }
        String formattedKeyword = "%" + keyword.toLowerCase() + "%";
        return ticketRepository.searchByKeyword(formattedKeyword);
    }
}
