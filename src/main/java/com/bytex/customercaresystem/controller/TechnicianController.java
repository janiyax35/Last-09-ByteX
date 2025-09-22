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
@org.springframework.web.bind.annotation.SessionAttributes("partRequestCart")
public class TechnicianController {

    private final UserService userService;
    private final RepairService repairService;
    private final com.bytex.customercaresystem.service.PartService partService;
    private final com.bytex.customercaresystem.service.PartRequestService partRequestService;
    private final com.bytex.customercaresystem.service.ResponseService responseService;
    private final com.bytex.customercaresystem.service.TicketService ticketService;

    public TechnicianController(UserService userService, RepairService repairService, com.bytex.customercaresystem.service.PartService partService, com.bytex.customercaresystem.service.PartRequestService partRequestService, com.bytex.customercaresystem.service.ResponseService responseService, com.bytex.customercaresystem.service.TicketService ticketService) {
        this.userService = userService;
        this.repairService = repairService;
        this.partService = partService;
        this.partRequestService = partRequestService;
        this.responseService = responseService;
        this.ticketService = ticketService;
    }

    // Initialize the session attribute
    @org.springframework.web.bind.annotation.ModelAttribute("partRequestCart")
    public java.util.Map<Long, Integer> partRequestCart() {
        return new java.util.LinkedHashMap<>();
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
    public String viewRepairDetails(@PathVariable Long id, Model model, @org.springframework.web.bind.annotation.ModelAttribute("partRequestCart") java.util.Map<Long, Integer> partRequestCart) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        java.util.Map<com.bytex.customercaresystem.model.Part, Integer> populatedCart = new java.util.LinkedHashMap<>();
        for(java.util.Map.Entry<Long, Integer> entry : partRequestCart.entrySet()){
            partService.findById(entry.getKey()).ifPresent(part -> populatedCart.put(part, entry.getValue()));
        }

        model.addAttribute("repair", repair);
        model.addAttribute("statuses", com.bytex.customercaresystem.model.RepairStatus.values());
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("newResponse", new com.bytex.customercaresystem.model.Response());
        model.addAttribute("populatedCart", populatedCart);
        model.addAttribute("pageTitle", "Repair Details");
        return "technician/repair-details";
    }

    @PostMapping("/repairs/{id}/status")
    public String updateRepairStatus(@PathVariable Long id, @RequestParam("status") com.bytex.customercaresystem.model.RepairStatus status, RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.updateRepairStatus(repair, status);
        redirectAttributes.addFlashAttribute("successMessage", "Repair status updated successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/details")
    public String addRepairDetails(@PathVariable Long id, @RequestParam("details") String details, RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.addRepairDetails(repair, details);
        redirectAttributes.addFlashAttribute("successMessage", "Repair details added successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/add-to-request")
    public String addToRequestCart(@PathVariable Long id, @RequestParam("partId") Long partId, @RequestParam("quantity") int quantity, @org.springframework.web.bind.annotation.ModelAttribute("partRequestCart") java.util.Map<Long, Integer> partRequestCart, RedirectAttributes redirectAttributes) {
        partRequestCart.put(partId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Part added to request list.");
        return "redirect:/technician/repairs/" + id;
    }

    @GetMapping("/repairs/{id}/remove-from-request/{partId}")
    public String removeFromRequestCart(@PathVariable Long id, @PathVariable Long partId, @org.springframework.web.bind.annotation.ModelAttribute("partRequestCart") java.util.Map<Long, Integer> partRequestCart) {
        partRequestCart.remove(partId);
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/submit-request")
    public String submitPartRequest(@PathVariable Long id, @org.springframework.web.bind.annotation.ModelAttribute("partRequestCart") java.util.Map<Long, Integer> partRequestCart, Authentication authentication, RedirectAttributes redirectAttributes, org.springframework.web.bind.support.SessionStatus sessionStatus) {
        User technician = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        if (partRequestCart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot submit an empty request.");
            return "redirect:/technician/repairs/" + id;
        }

        for (java.util.Map.Entry<Long, Integer> entry : partRequestCart.entrySet()) {
            com.bytex.customercaresystem.model.Part part = partService.findById(entry.getKey()).orElseThrow(() -> new IllegalArgumentException("Invalid part Id in cart:" + entry.getKey()));
            partRequestService.createPartRequest(technician, part, entry.getValue(), "Part required for repair #" + id, repair);
        }

        ticketService.updateTicketStage(repair.getTicket().getTicketId(), com.bytex.customercaresystem.model.TicketStage.AWAITING_PARTS);

        sessionStatus.setComplete(); // Clears the session attributes
        redirectAttributes.addFlashAttribute("successMessage", "Part request submitted successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/reply")
    public String addReply(@PathVariable Long id, com.bytex.customercaresystem.model.Response newResponse, Authentication authentication, RedirectAttributes redirectAttributes) {
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
