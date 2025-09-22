package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    /**
     * Saves a ticket (either new or updated).
     * @param ticket The ticket to save.
     * @return The saved ticket.
     */
    Ticket saveTicket(Ticket ticket);

    /**
     * Finds a ticket by its ID.
     * @param id The ID of the ticket.
     * @return An Optional containing the ticket if found.
     */
    Optional<Ticket> findById(Long id);

    /**
     * Finds all tickets in the system.
     * @return A list of all tickets.
     */
    List<Ticket> findAllTickets();

    /**
     * Finds all tickets created by a specific customer.
     * @param customer The customer user.
     * @return A list of the customer's tickets.
     */
    List<Ticket> findTicketsByCustomer(User customer);

    /**
     * Allows a customer to create a new ticket.
     * @param ticket The ticket object from the form.
     * @param customer The currently logged-in customer.
     * @return The newly created ticket.
     */
    Ticket createTicket(Ticket ticket, User customer);

    /**
     * Allows a customer to cancel their ticket, subject to business rules.
     * @param ticketId The ID of the ticket to cancel.
     * @param customer The currently logged-in customer.
     * @throws Exception if the ticket cannot be canceled.
     */
    void cancelTicket(Long ticketId, User customer) throws Exception;

    /**
     * Finds all tickets assigned to a specific staff member.
     * @param staffMember The staff user.
     * @return A list of tickets.
     */
    List<Ticket> findTicketsByAssignedTo(User staffMember);

    /**
     * Finds all tickets that are not yet assigned to any staff member.
     * @return A list of unassigned tickets.
     */
    List<Ticket> findUnassignedTickets();

    /**
     * Assigns a ticket to a staff member.
     * @param ticket The ticket to be accepted.
     * @param staffMember The staff member accepting the ticket.
     * @return The updated ticket.
     */
    Ticket acceptTicket(Ticket ticket, User staffMember);

    /**
     * Updates the status of a ticket.
     * @param ticket The ticket to update.
     * @param status The new status.
     * @return The updated ticket.
     */
    Ticket updateTicketStatus(Ticket ticket, com.bytex.customercaresystem.model.TicketStatus status);

    /**
     * Archives a ticket.
     * @param ticket The ticket to archive.
     * @return The archived ticket.
     */
    Ticket archiveTicket(Ticket ticket);

    /**
     * Escalates a ticket to a technician.
     * @param ticket The ticket to escalate.
     * @param technician The technician to assign.
     * @param diagnosis The initial diagnosis.
     * @return The updated ticket.
     */
    Ticket escalateTicket(Ticket ticket, User technician, String diagnosis);
}
