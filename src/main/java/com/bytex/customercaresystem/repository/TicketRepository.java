package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.TicketStatus;
import com.bytex.customercaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Finds all tickets associated with a specific customer.
     * @param customer The customer user.
     * @return A list of tickets.
     */
    List<Ticket> findByCustomer(User customer);

    /**
     * Finds all tickets assigned to a specific staff member.
     * @param assignedTo The staff user.
     * @return A list of tickets.
     */
    List<Ticket> findByAssignedTo(User assignedTo);

    /**
     * Finds all tickets with a specific status.
     * @param status The ticket status enum.
     * @return A list of tickets.
     */
    List<Ticket> findByStatus(TicketStatus status);

    /**
     * Finds all tickets that have not yet been assigned to any staff member.
     * @return A list of unassigned tickets.
     */
    List<Ticket> findByAssignedToIsNull();

    /**
     * Searches for tickets where the keyword is contained in the subject, description, customer's full name,
     * or assigned staff's full name, ignoring case.
     * @param keyword The keyword for the subject search.
     * @param keyword2 The keyword for the description search.
     * @param keyword3 The keyword for the customer name search.
     * @param keyword4 The keyword for the assigned staff name search.
     * @return A list of matching tickets.
     */
    List<Ticket> findBySubjectContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCustomerFullNameContainingIgnoreCaseOrAssignedToFullNameContainingIgnoreCase(
        String keyword, String keyword2, String keyword3, String keyword4
    );
}
