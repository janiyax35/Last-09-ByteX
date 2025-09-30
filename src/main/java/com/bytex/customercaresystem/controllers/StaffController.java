package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.models.enums.TicketStatus;
import com.bytex.customercaresystem.models.enums.UserRole;
import com.bytex.customercaresystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private RepairService repairService;

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Model model, Principal principal) {
        User staffMember = getCurrentUser(principal);
        List<Ticket> allTickets = ticketService.findAllTickets();

        List<Ticket> unassignedTickets = allTickets.stream()
                .filter(t -> t.getAssignedTo() == null && t.getStatus() == TicketStatus.OPEN)
                .collect(Collectors.toList());

        List<Ticket> myTickets = allTickets.stream()
                .filter(t -> staffMember.equals(t.getAssignedTo()))
                .collect(Collectors.toList());

        model.addAttribute("unassignedTickets", unassignedTickets);
        model.addAttribute("myTickets", myTickets);
        return "staff/dashboard";
    }

    @GetMapping("/tickets/accept/{id}")
    public String acceptTicket(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        ticket.setAssignedTo(staffMember);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket #" + id + " has been assigned to you.");
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/tickets/view/{id}")
    public String viewTicket(@PathVariable Long id, Model model, Principal principal) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        // Security check: Only assigned staff can view
        if (!staffMember.equals(ticket.getAssignedTo())) {
            return "redirect:/staff/dashboard?error=not_assigned";
        }

        List<User> technicians = userService.findAll().stream()
            .filter(u -> u.getRole() == UserRole.TECHNICIAN)
            .collect(Collectors.toList());

        model.addAttribute("ticket", ticket);
        model.addAttribute("technicians", technicians);
        model.addAttribute("newResponse", ""); // For the response form
        return "staff/ticket-view";
    }

    @PostMapping("/tickets/respond/{id}")
    public String respondToTicket(@PathVariable Long id, @RequestParam String message, Principal principal, RedirectAttributes redirectAttributes) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        if (!staffMember.equals(ticket.getAssignedTo())) {
            return "redirect:/staff/dashboard?error=not_assigned";
        }

        responseService.createResponse(ticket, staffMember, message);
        redirectAttributes.addFlashAttribute("successMessage", "Your response has been sent.");
        return "redirect:/staff/tickets/view/" + id;
    }

    @PostMapping("/tickets/update-status/{id}")
    public String updateTicketStatus(@PathVariable Long id, @RequestParam("status") TicketStatus status, Principal principal, RedirectAttributes redirectAttributes) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        if (!staffMember.equals(ticket.getAssignedTo())) {
            return "redirect:/staff/dashboard?error=not_assigned";
        }

        ticket.setStatus(status);
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket status updated to " + status.name());
        return "redirect:/staff/tickets/view/" + id;
    }

    @PostMapping("/tickets/escalate/{id}")
    public String escalateToTechnician(@PathVariable Long id, @RequestParam Long technicianId, @RequestParam String diagnosis, Principal principal, RedirectAttributes redirectAttributes) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
        User technician = userService.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid technician Id:" + technicianId));

        if (!staffMember.equals(ticket.getAssignedTo())) {
            return "redirect:/staff/dashboard?error=not_assigned";
        }

        // Create a repair record
        repairService.createRepair(ticket, technician, diagnosis);

        // Update ticket status
        ticket.setStatus(TicketStatus.PENDING); // Or a new status like 'ESCALATED'
        ticket.setStage("TECHNICIAN_ASSIGNED");
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket escalated to technician " + technician.getFullName());
        return "redirect:/staff/dashboard";
    }

    @GetMapping("/tickets/archive/{id}")
    public String archiveTicket(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User staffMember = getCurrentUser(principal);
        Ticket ticket = ticketService.findTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        if (!staffMember.equals(ticket.getAssignedTo())) {
            return "redirect:/staff/dashboard?error=not_assigned";
        }

        if (ticket.getStatus() != TicketStatus.RESOLVED && ticket.getStatus() != TicketStatus.CLOSED) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only resolved or closed tickets can be archived.");
            return "redirect:/staff/tickets/view/" + id;
        }

        ticket.setArchived(true);
        ticket.setArchivedAt(LocalDateTime.now());
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Ticket #" + id + " has been archived.");
        return "redirect:/staff/dashboard";
    }
}