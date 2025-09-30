package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.*;
import com.bytex.customercaresystem.models.enums.PartRequestStatus;
import com.bytex.customercaresystem.models.enums.RepairStatus;
import com.bytex.customercaresystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired
    private RepairService repairService;
    @Autowired
    private UserService userService;
    @Autowired
    private PartService partService;
    @Autowired
    private PartRequestService partRequestService;
    @Autowired
    private ResponseService responseService;
    @Autowired
    private TicketService ticketService;

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @GetMapping("/dashboard")
    public String technicianDashboard(Model model, Principal principal) {
        User technician = getCurrentUser(principal);
        List<Repair> repairs = repairService.findByTechnician(technician);
        model.addAttribute("repairs", repairs);
        return "technician/dashboard";
    }

    @GetMapping("/repairs/view/{id}")
    public String viewRepair(@PathVariable Long id, Model model, Principal principal) {
        User technician = getCurrentUser(principal);
        Repair repair = repairService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        // Security check
        if (!repair.getTechnician().getId().equals(technician.getId())) {
            return "redirect:/technician/dashboard?error=access_denied";
        }

        model.addAttribute("repair", repair);
        model.addAttribute("ticket", repair.getTicket());
        model.addAttribute("parts", partService.findAllParts());
        model.addAttribute("newPartRequest", new PartRequest());
        return "technician/repair-view";
    }

    @PostMapping("/repairs/update-status/{id}")
    public String updateRepairStatus(@PathVariable Long id, @RequestParam RepairStatus status, Principal principal, RedirectAttributes redirectAttributes) {
        User technician = getCurrentUser(principal);
        Repair repair = repairService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        if (!repair.getTechnician().getId().equals(technician.getId())) {
            return "redirect:/technician/dashboard?error=access_denied";
        }

        repair.setStatus(status);
        repairService.saveRepair(repair);

        // Also update the main ticket's stage
        Ticket ticket = repair.getTicket();
        if (status == RepairStatus.COMPLETED) {
            ticket.setStage("REPAIR_COMPLETED");
        } else {
            ticket.setStage("REPAIR_IN_PROGRESS");
        }
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Repair status updated.");
        return "redirect:/technician/repairs/view/" + id;
    }

    @PostMapping("/repairs/request-part/{repairId}")
    public String requestPart(@PathVariable Long repairId, @ModelAttribute PartRequest partRequest, Principal principal, RedirectAttributes redirectAttributes) {
        User technician = getCurrentUser(principal);
        Repair repair = repairService.findById(repairId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + repairId));
        Part part = partService.findPartById(partRequest.getPart().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid part Id."));

        if (!repair.getTechnician().getId().equals(technician.getId())) {
            return "redirect:/technician/dashboard?error=access_denied";
        }

        partRequest.setRequestor(technician);
        partRequest.setPart(part);
        partRequest.setStatus(PartRequestStatus.PENDING);
        partRequestService.createPartRequest(partRequest);

        // Update repair and ticket status
        repair.setStatus(RepairStatus.WAITING_FOR_PARTS);
        repairService.saveRepair(repair);

        Ticket ticket = repair.getTicket();
        ticket.setStage("AWAITING_PARTS");
        ticketService.saveTicket(ticket);

        redirectAttributes.addFlashAttribute("successMessage", "Part request for '" + part.getPartName() + "' has been submitted.");
        return "redirect:/technician/repairs/view/" + repairId;
    }

    @PostMapping("/repairs/respond/{repairId}")
    public String respondToTicket(@PathVariable Long repairId, @RequestParam String message, Principal principal, RedirectAttributes redirectAttributes) {
        User technician = getCurrentUser(principal);
        Repair repair = repairService.findById(repairId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + repairId));

        if (!repair.getTechnician().getId().equals(technician.getId())) {
            return "redirect:/technician/dashboard?error=access_denied";
        }

        responseService.createResponse(repair.getTicket(), technician, message);
        redirectAttributes.addFlashAttribute("successMessage", "Your response has been sent.");
        return "redirect:/technician/repairs/view/" + repairId;
    }
}