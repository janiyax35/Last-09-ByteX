package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.RepairService;
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
@RequestMapping("/technician")
public class TechnicianController {

    private final UserService userService;
    private final RepairService repairService;
    private final com.bytex.customercaresystem.service.PartService partService;
    private final com.bytex.customercaresystem.service.PartRequestService partRequestService;
    private final com.bytex.customercaresystem.service.ResponseService responseService;

    public TechnicianController(UserService userService, RepairService repairService, com.bytex.customercaresystem.service.PartService partService, com.bytex.customercaresystem.service.PartRequestService partRequestService, com.bytex.customercaresystem.service.ResponseService responseService) {
        this.userService = userService;
        this.repairService = repairService;
        this.partService = partService;
        this.partRequestService = partRequestService;
        this.responseService = responseService;
    }

    private User getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @GetMapping("/dashboard")
    public String technicianDashboard(Model model, Authentication authentication) {
        User technician = getLoggedInUser(authentication);
        model.addAttribute("repairs", repairService.findRepairsByTechnician(technician));
        model.addAttribute("pageTitle", "Technician Dashboard");
        return "technician/dashboard";
    }

    @GetMapping("/repairs/{id}")
    public String viewRepairDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        model.addAttribute("repair", repair);
        model.addAttribute("statuses", com.bytex.customercaresystem.model.RepairStatus.values());
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        model.addAttribute("pageTitle", "Repair Details");
        return "technician/repair-details";
    }

    @PostMapping("/repairs/{id}/status")
    public String updateRepairStatus(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("status") com.bytex.customercaresystem.model.RepairStatus status, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.updateRepairStatus(repair, status);
        redirectAttributes.addFlashAttribute("successMessage", "Repair status updated successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/details")
    public String addRepairDetails(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("details") String details, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.addRepairDetails(repair, details);
        redirectAttributes.addFlashAttribute("successMessage", "Repair details added successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/request-part")
    public String requestPart(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("partId") Long partId, @org.springframework.web.bind.annotation.RequestParam("quantity") int quantity, @org.springframework.web.bind.annotation.RequestParam("reason") String reason, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User technician = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        com.bytex.customercaresystem.model.Part part = partService.findById(partId).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + partId));
        partRequestService.createPartRequest(technician, part, quantity, reason, repair);
        redirectAttributes.addFlashAttribute("successMessage", "Part request submitted successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/reply")
    public String addReply(@org.springframework.web.bind.annotation.PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User technician = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        try {
            responseService.saveResponse(newResponse, technician, repair.getTicket());
            redirectAttributes.addFlashAttribute("successMessage", "Reply posted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting reply: " + e.getMessage());
        }
        return "redirect:/technician/repairs/" + id;
    }
}
