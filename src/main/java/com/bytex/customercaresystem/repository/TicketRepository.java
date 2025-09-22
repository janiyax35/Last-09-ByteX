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
public interface TicketRepository extends JpaRepository<Ticket, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Ticket> {

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
}
