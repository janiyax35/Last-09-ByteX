package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.TicketService;
import com.bytex.customercaresystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final TicketService ticketService;
    private final UserService userService;
    private final com.bytex.customercaresystem.service.ResponseService responseService;

    public CustomerController(TicketService ticketService, UserService userService, com.bytex.customercaresystem.service.ResponseService responseService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.responseService = responseService;
    }

    private User getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
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
        model.addAttribute("priorities", com.bytex.customercaresystem.model.TicketPriority.values());
        model.addAttribute("pageTitle", "Create New Ticket");
        return "customer/ticket-form";
    }

    @PostMapping("/tickets/new")
    public String createTicket(Ticket ticket, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            User customer = getLoggedInUser(authentication);
            ticketService.createTicket(ticket, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Your ticket has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating ticket: " + e.getMessage());
            return "redirect:/customer/tickets/new";
        }
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, Authentication authentication) {
        User customer = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        // Security check: ensure the customer owns this ticket
        if (!ticket.getCustomer().getUserId().equals(customer.getUserId())) {
            return "redirect:/customer/dashboard?error=access_denied";
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        model.addAttribute("pageTitle", "Ticket Details");
        return "customer/ticket-details";
    }

    @PostMapping("/tickets/{id}/reply")
    public String addReply(@org.springframework.web.bind.annotation.PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse,
                         Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            User customer = getLoggedInUser(authentication);
            Ticket ticket = ticketService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

            // The service method will perform the final authorization check
            this.responseService.saveResponse(newResponse, customer, ticket);

            redirectAttributes.addFlashAttribute("successMessage", "Your reply has been posted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting reply: " + e.getMessage());
        }
        return "redirect:/customer/tickets/" + id;
    }

    @GetMapping("/tickets/{id}/cancel")
    public String cancelTicket(@org.springframework.web.bind.annotation.PathVariable Long id, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            User customer = getLoggedInUser(authentication);
            ticketService.cancelTicket(id, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Your ticket has been successfully canceled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error canceling ticket: " + e.getMessage());
            return "redirect:/customer/tickets/" + id;
        }
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/tickets/{id}/edit")
    public String showEditTicketForm(@PathVariable Long id, Model model, Authentication authentication) {
        User customer = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        // Security check: ensure the customer owns this ticket and it is open
        if (!ticket.getCustomer().getUserId().equals(customer.getUserId()) || ticket.getStatus() != com.bytex.customercaresystem.model.TicketStatus.OPEN) {
            return "redirect:/customer/dashboard?error=access_denied";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("priorities", com.bytex.customercaresystem.model.TicketPriority.values());
        model.addAttribute("pageTitle", "Edit Ticket");
        return "customer/ticket-form"; // Re-use the same form
    }

    @PostMapping("/tickets/{id}/edit")
    public String updateTicket(@PathVariable Long id, Ticket ticket, Authentication authentication, RedirectAttributes redirectAttributes) {
        User customer = getLoggedInUser(authentication);
        Ticket existingTicket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        if (!existingTicket.getCustomer().getUserId().equals(customer.getUserId()) || existingTicket.getStatus() != com.bytex.customercaresystem.model.TicketStatus.OPEN) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this ticket.");
            return "redirect:/customer/dashboard";
        }
        // Update fields
        existingTicket.setSubject(ticket.getSubject());
        existingTicket.setDescription(ticket.getDescription());
        existingTicket.setPriority(ticket.getPriority());
        ticketService.saveTicket(existingTicket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully.");
        return "redirect:/customer/tickets/" + id;
    }
}
