package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.RepairStatus;
import com.bytex.customercaresystem.model.TicketStage;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/technician")
@SessionAttributes("partRequestCart")
public class TechnicianController {

    private final UserService userService;
    private final RepairService repairService;
    private final PartService partService;
    private final PartRequestService partRequestService;
    private final TicketService ticketService;

    public TechnicianController(UserService userService, RepairService repairService, PartService partService, PartRequestService partRequestService, TicketService ticketService) {
        this.userService = userService;
        this.repairService = repairService;
        this.partService = partService;
        this.partRequestService = partRequestService;
        this.ticketService = ticketService;
    }

    @ModelAttribute("partRequestCart")
    public Map<Long, Integer> partRequestCart() {
        return new LinkedHashMap<>();
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @GetMapping("/dashboard")
    public String technicianDashboard(Model model, Authentication authentication) {
        User technician = getLoggedInUser(authentication);
        model.addAttribute("repairs", repairService.findRepairsByTechnician(technician));
        model.addAttribute("pageTitle", "My Repair Jobs");
        return "technician/dashboard";
    }

    @GetMapping("/repairs/{id}")
    public String viewRepairDetails(@PathVariable Long id, Model model) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        Map<com.bytex.customercaresystem.model.Part, Integer> populatedCart = new LinkedHashMap<>();
        if (model.containsAttribute("partRequestCart")) {
            Map<Long, Integer> cart = (Map<Long, Integer>) model.getAttribute("partRequestCart");
            cart.forEach((partId, quantity) -> partService.findById(partId).ifPresent(part -> populatedCart.put(part, quantity)));
        }

        model.addAttribute("repair", repair);
        model.addAttribute("statuses", RepairStatus.values());
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("populatedCart", populatedCart);
        model.addAttribute("pageTitle", "Repair Details");
        return "technician/repair-details";
    }

    @PostMapping("/repairs/{id}/add-to-request")
    public String addToRequestCart(@PathVariable Long id, @RequestParam Long partId, @RequestParam int quantity, @ModelAttribute("partRequestCart") Map<Long, Integer> partRequestCart, RedirectAttributes redirectAttributes) {
        partRequestCart.put(partId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Part added to request list.");
        return "redirect:/technician/repairs/" + id;
    }

    @GetMapping("/repairs/{id}/remove-from-request/{partId}")
    public String removeFromRequestCart(@PathVariable Long id, @PathVariable Long partId, @ModelAttribute("partRequestCart") Map<Long, Integer> partRequestCart) {
        partRequestCart.remove(partId);
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/submit-request")
    public String submitPartRequest(@PathVariable Long id, @ModelAttribute("partRequestCart") Map<Long, Integer> partRequestCart, Authentication authentication, RedirectAttributes redirectAttributes, SessionStatus sessionStatus) {
        User technician = getLoggedInUser(authentication);
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));

        if (partRequestCart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot submit an empty request.");
            return "redirect:/technician/repairs/" + id;
        }

        partRequestCart.forEach((partId, quantity) -> {
            partService.findById(partId).ifPresent(part ->
                partRequestService.createPartRequest(technician, part, quantity, "Part required for repair #" + id, repair)
            );
        });

        ticketService.updateTicketStage(repair.getTicket().getTicketId(), TicketStage.AWAITING_PARTS);

        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("successMessage", "Part request submitted successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/status")
    public String updateRepairStatus(@PathVariable Long id, @RequestParam RepairStatus status, RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.updateRepairStatus(repair, status);
        redirectAttributes.addFlashAttribute("successMessage", "Repair status updated successfully.");
        return "redirect:/technician/repairs/" + id;
    }

    @PostMapping("/repairs/{id}/details")
    public String addRepairDetails(@PathVariable Long id, @RequestParam String details, RedirectAttributes redirectAttributes) {
        com.bytex.customercaresystem.model.Repair repair = repairService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid repair Id:" + id));
        repairService.addRepairDetails(repair, details);
        redirectAttributes.addFlashAttribute("successMessage", "Repair details added successfully.");
        return "redirect:/technician/repairs/" + id;
    }
}
