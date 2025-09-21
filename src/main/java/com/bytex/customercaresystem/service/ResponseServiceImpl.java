package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Response;
import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.ResponseRepository;
import org.springframework.stereotype.Service;

@Service
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;

    public ResponseServiceImpl(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    @Override
    public Response saveResponse(Response response, User user, Ticket ticket) throws Exception {
        // Security Check: Ensure the user is either the customer or an authorized staff member
        boolean isCustomerOwner = ticket.getCustomer().getUserId().equals(user.getUserId());
        boolean isAssignedStaff = ticket.getAssignedTo() != null && ticket.getAssignedTo().getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole() == Role.ADMIN; // Admins can probably comment on anything

        if (!isCustomerOwner && !isAssignedStaff && !isAdmin) {
            throw new Exception("You are not authorized to comment on this ticket.");
        }

        response.setTicket(ticket);
        response.setUser(user);
        return responseRepository.save(response);
    }
}
