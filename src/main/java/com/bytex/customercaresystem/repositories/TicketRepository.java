package com.bytex.customercaresystem.repositories;

import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByCustomer(User customer);
    List<Ticket> findByAssignedTo(User assignedTo);
}