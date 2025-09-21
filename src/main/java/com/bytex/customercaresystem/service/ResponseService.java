package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Response;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

public interface ResponseService {

    /**
     * Saves a new response to a ticket.
     * @param response The response object to save.
     * @param user The user posting the response.
     * @param ticket The ticket the response is for.
     * @return The saved response.
     * @throws Exception if the ticket is not found or user is not authorized.
     */
    Response saveResponse(Response response, User user, Ticket ticket) throws Exception;

}
