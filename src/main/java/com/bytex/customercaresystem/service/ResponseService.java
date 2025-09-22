package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Response;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

public interface ResponseService {
    Response saveResponse(Response response, User user, Ticket ticket);
}
