package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Role;
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

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final TicketService ticketService;
    private final UserService userService;
    private final ResponseService responseService;

    public StaffController(TicketService ticketService, UserService userService, ResponseService responseService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.responseService = responseService;
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Model model) {
        model.addAttribute("unassignedTickets", ticketService.findUnassignedTickets());
        model.addAttribute("pageTitle", "Staff Dashboard");
        return "staff/dashboard";
    }

    @GetMapping("/my-tickets")
    public String myTickets(Model model, Authentication authentication, @RequestParam(required = false) String keyword) {
        User staffMember = getLoggedInUser(authentication);
        model.addAttribute("myTickets", ticketService.searchTickets(keyword, null, staffMember));
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "My Assigned Tickets");
        return "staff/my-tickets";
    }

    @GetMapping("/tickets/{id}/accept")
    public String acceptTicket(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User staffMember = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        ticketService.acceptTicket(ticket, staffMember);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket #" + id + " assigned to you.");
        return "redirect:/staff/my-tickets";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicketDetails(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        model.addAttribute("ticket", ticket);
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        model.addAttribute("technicians", userService.findUsersByRole(Role.TECHNICIAN));
        model.addAttribute("staffMembers", userService.findUsersByRole(Role.STAFF));
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("pageTitle", "Ticket Details");
        return "staff/ticket-details";
    }

    @PostMapping("/tickets/{id}/escalate")
    public String escalateTicket(@PathVariable Long id, @RequestParam Long technicianId, @RequestParam String diagnosis, RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        User technician = userService.findById(technicianId).orElseThrow(() -> new IllegalArgumentException("Invalid technician ID"));
        ticketService.escalateTicket(ticket, technician, diagnosis);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket escalated to " + technician.getFullName());
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/reply")
    public String addReply(@PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse, Authentication authentication, RedirectAttributes redirectAttributes) {
        User staffMember = getLoggedInUser(authentication);
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        responseService.saveResponse(newResponse, staffMember, ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Reply posted.");
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam TicketStatus status, RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        ticketService.updateTicketStatus(ticket, status);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket status updated.");
        return "redirect:/staff/tickets/" + id;
    }
}
