package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.services.TicketService;
import com.bytex.customercaresystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @GetMapping("/dashboard")
    public String customerDashboard(Model model, Principal principal) {
        User customer = getCurrentUser(principal);
        List<Ticket> tickets = ticketService.findTicketsByCustomer(customer);
        model.addAttribute("tickets", tickets);
        return "customer/dashboard";
    }

    @GetMapping("/tickets")
    public String listMyTickets(Model model, Principal principal) {
        User customer = getCurrentUser(principal);
        model.addAttribute("tickets", ticketService.findTicketsByCustomer(customer));
        return "customer/tickets";
    }

    @GetMapping("/tickets/new")
    public String newTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "customer/ticket-form";
    }

    @PostMapping("/tickets/new")
    public String createTicket(@ModelAttribute("ticket") Ticket ticket, BindingResult result, Principal principal, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customer/ticket-form";
        }
        User customer = getCurrentUser(principal);
        ticketService.createTicket(ticket, customer);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket created successfully!");
        return "redirect:/customer/tickets";
    }

    @GetMapping("/tickets/view/{id}")
    public String viewTicket(@PathVariable Long id, Model model, Principal principal) {
        User customer = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        // Security check: ensure the customer owns this ticket
        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            return "redirect:/customer/tickets?error=access_denied";
        }

        model.addAttribute("ticket", ticket);
        return "customer/ticket-view";
    }

    @GetMapping("/tickets/edit/{id}")
    public String editTicketForm(@PathVariable Long id, Model model, Principal principal) {
        User customer = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        // Security check
        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            return "redirect:/customer/tickets?error=access_denied";
        }
        // Business rule: Can only edit if not yet assigned
        if (ticket.getAssignedTo() != null) {
             return "redirect:/customer/tickets/view/" + id + "?error=edit_locked";
        }

        model.addAttribute("ticket", ticket);
        return "customer/ticket-form";
    }

    @PostMapping("/tickets/edit/{id}")
    public String updateTicket(@PathVariable Long id, @ModelAttribute("ticket") Ticket ticketDetails, Principal principal, RedirectAttributes redirectAttributes) {
        User customer = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            return "redirect:/customer/tickets?error=access_denied";
        }
        if (ticket.getAssignedTo() != null) {
            return "redirect:/customer/tickets/view/" + id + "?error=edit_locked";
        }

        // Update fields
        ticket.setSubject(ticketDetails.getSubject());
        ticket.setDescription(ticketDetails.getDescription());
        ticket.setPriority(ticketDetails.getPriority());
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully!");
        return "redirect:/customer/tickets/view/" + id;
    }

    @GetMapping("/tickets/delete/{id}")
    public String deleteTicket(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User customer = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            return "redirect:/customer/tickets?error=access_denied";
        }
        // Business rule: Can only delete if not yet assigned
        if (ticket.getAssignedTo() != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete a ticket that is already being handled.");
            return "redirect:/customer/tickets/view/" + id;
        }

        ticketService.deleteTicket(id);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket has been successfully deleted.");
        return "redirect:/customer/tickets";
    }
}