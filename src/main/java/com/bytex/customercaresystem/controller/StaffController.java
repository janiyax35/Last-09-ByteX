package com.bytex.customercaresystem.controller;

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

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final TicketService ticketService;
    private final UserService userService;
    private final com.bytex.customercaresystem.service.RepairService repairService;
    private final com.bytex.customercaresystem.service.ResponseService responseService;


    public StaffController(TicketService ticketService, UserService userService, com.bytex.customercaresystem.service.RepairService repairService, com.bytex.customercaresystem.service.ResponseService responseService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.repairService = repairService;
        this.responseService = responseService;
    }

    private User getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Model model, Authentication authentication) {
        User staffMember = getLoggedInUser(authentication);
        model.addAttribute("unassignedTickets", ticketService.findUnassignedTickets());
        model.addAttribute("myTickets", ticketService.findTicketsByAssignedTo(staffMember));
        model.addAttribute("pageTitle", "Staff Dashboard");
        return "staff/dashboard";
    }

    @GetMapping("/tickets/{id}/accept")
    public String acceptTicket(@org.springframework.web.bind.annotation.PathVariable Long id, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User staffMember = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        ticketService.acceptTicket(ticket, staffMember);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket #" + id + " has been assigned to you.");
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicketDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        model.addAttribute("ticket", ticket);
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        model.addAttribute("technicians", userService.findUsersByRole(com.bytex.customercaresystem.model.Role.TECHNICIAN));
        model.addAttribute("statuses", com.bytex.customercaresystem.model.TicketStatus.values());
        model.addAttribute("pageTitle", "Ticket Details");
        return "staff/ticket-details";
    }

    @PostMapping("/tickets/{id}/reply")
    public String addReply(@org.springframework.web.bind.annotation.PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User staffMember = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        try {
            responseService.saveResponse(newResponse, staffMember, ticket);
            redirectAttributes.addFlashAttribute("successMessage", "Reply posted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting reply: " + e.getMessage());
        }
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/status")
    public String updateStatus(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("status") com.bytex.customercaresystem.model.TicketStatus status, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        ticketService.updateTicketStatus(ticket, status);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket status updated successfully.");
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/escalate")
    public String escalateTicket(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("technicianId") Long technicianId, @org.springframework.web.bind.annotation.RequestParam("diagnosis") String diagnosis, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        User technician = userService.findById(technicianId).orElseThrow(() -> new IllegalArgumentException("Invalid technician Id:" + technicianId));

        repairService.createRepair(ticket, technician, diagnosis);

        // Also update ticket status to show it's with a technician
        ticketService.updateTicketStatus(ticket, com.bytex.customercaresystem.model.TicketStatus.IN_PROGRESS); // Or a new status like 'ESCALATED'

        redirectAttributes.addFlashAttribute("successMessage", "Ticket successfully escalated to " + technician.getFullName());
        return "redirect:/staff/tickets/" + id;
    }

    @GetMapping("/tickets/{id}/archive")
    public String archiveTicket(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        ticketService.archiveTicket(ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket #" + id + " has been archived.");
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/my-tickets")
    public String viewMyTickets(Model model, Authentication authentication) {
        User staffMember = getLoggedInUser(authentication);
        model.addAttribute("myTickets", ticketService.findTicketsByAssignedTo(staffMember));
        model.addAttribute("pageTitle", "My Assigned Tickets");
        return "staff/my-tickets";
    }
}
