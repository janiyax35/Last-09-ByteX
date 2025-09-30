package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.Response;
import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.repositories.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Transactional
    public Response createResponse(Ticket ticket, User user, String message) {
        Response response = new Response();
        response.setTicket(ticket);
        response.setUser(user);
        response.setMessage(message);
        return responseRepository.save(response);
    }
}