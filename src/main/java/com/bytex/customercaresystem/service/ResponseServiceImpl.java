package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Response;
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
    public Response saveResponse(Response response, User user, Ticket ticket) {
        response.setUser(user);
        response.setTicket(ticket);
        return responseRepository.save(response);
    }
}
