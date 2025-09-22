package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.TicketStatus;
import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket saveTicket(Ticket ticket);
    Optional<Ticket> findById(Long id);
    List<Ticket> findAllTickets();
    List<Ticket> findTicketsByCustomer(User customer);
    Ticket createTicket(Ticket ticket, User customer);
    void cancelTicket(Long ticketId, User customer) throws Exception;
    List<Ticket> findTicketsByAssignedTo(User staffMember);
    List<Ticket> findUnassignedTickets();
    Ticket acceptTicket(Ticket ticket, User staffMember);
    Ticket updateTicketStatus(Ticket ticket, TicketStatus status);
    Ticket archiveTicket(Ticket ticket);
    Ticket escalateTicket(Ticket ticket, User technician, String diagnosis);
    Ticket updateTicketStage(Long ticketId, com.bytex.customercaresystem.model.TicketStage stage);
    List<Ticket> searchTickets(String keyword, User customer, User assignedTo);
}
