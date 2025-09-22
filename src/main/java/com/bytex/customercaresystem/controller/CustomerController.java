package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.TicketStatus;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.ResponseService;
import com.bytex.customercaresystem.service.TicketService;
import com.bytex.customercaresystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final TicketService ticketService;
    private final UserService userService;
    private final ResponseService responseService;

    public CustomerController(TicketService ticketService, UserService userService, ResponseService responseService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.responseService = responseService;
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @GetMapping("/dashboard")
    public String customerDashboard(Model model, Authentication authentication, @RequestParam(required = false) String keyword) {
        User customer = getLoggedInUser(authentication);
        List<Ticket> tickets = ticketService.searchTickets(keyword, customer, null);
        model.addAttribute("tickets", tickets);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "My Dashboard");
        return "customer/dashboard";
    }

    @GetMapping("/tickets/new")
    public String showNewTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        return "customer/ticket-form";
    }

    @PostMapping("/tickets/new")
    public String createTicket(Ticket ticket, Authentication authentication, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(authentication);
        ticketService.createTicket(ticket, customer);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket created successfully!");
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, Model model, Authentication authentication) {
        User customer = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        if (!ticket.getCustomer().equals(customer)) {
            return "redirect:/customer/dashboard?error=access_denied";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        return "customer/ticket-details";
    }

    @GetMapping("/tickets/{id}/edit")
    public String showEditTicketForm(@PathVariable Long id, Model model, Authentication authentication) {
        User customer = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        if (!ticket.getCustomer().equals(customer) || ticket.getStatus() != TicketStatus.OPEN) {
            return "redirect:/customer/dashboard?error=unauthorized_edit";
        }
        model.addAttribute("ticket", ticket);
        return "customer/ticket-form";
    }

    @PostMapping("/tickets/{id}/edit")
    public String updateTicket(@PathVariable Long id, Ticket ticket, Authentication authentication, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(authentication);
        Ticket existingTicket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        if (!existingTicket.getCustomer().equals(customer) || existingTicket.getStatus() != TicketStatus.OPEN) {
            redirectAttributes.addFlashAttribute("errorMessage", "This ticket cannot be edited.");
            return "redirect:/customer/dashboard";
        }
        existingTicket.setSubject(ticket.getSubject());
        existingTicket.setDescription(ticket.getDescription());
        existingTicket.setPriority(ticket.getPriority());
        ticketService.saveTicket(existingTicket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully.");
        return "redirect:/customer/tickets/" + id;
    }

    @GetMapping("/tickets/{id}/delete")
    public String deleteTicket(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            User customer = getLoggedInUser(authentication);
            ticketService.cancelTicket(id, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket successfully canceled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    @PostMapping("/tickets/{id}/reply")
    public String addReply(@PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse, Authentication authentication, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        if (!ticket.getCustomer().equals(customer)) {
             redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to reply to this ticket.");
             return "redirect:/customer/dashboard";
        }
        responseService.saveResponse(newResponse, customer, ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Reply posted.");
        return "redirect:/customer/tickets/" + id;
    }
}
