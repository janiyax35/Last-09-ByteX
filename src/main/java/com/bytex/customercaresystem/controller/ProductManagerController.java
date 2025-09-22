package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.service.PartRequestService;
import com.bytex.customercaresystem.service.PartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productmanager")
public class ProductManagerController {

    private final PartService partService;
    private final PartRequestService partRequestService;

    public ProductManagerController(PartService partService, PartRequestService partRequestService) {
        this.partService = partService;
        this.partRequestService = partRequestService;
    }

    @GetMapping("/dashboard")
    public String productManagerDashboard(Model model) {
        model.addAttribute("pendingRequests", partRequestService.findPendingRequests());
        model.addAttribute("lowStockParts", partService.findLowStockParts());
        model.addAttribute("pageTitle", "PM Dashboard");
        return "productmanager/dashboard";
    }

    @GetMapping("/parts")
    public String manageParts(Model model) {
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("pageTitle", "Manage Parts");
        return "productmanager/manage-parts";
    }

    @GetMapping("/parts/add")
    public String showAddPartForm(Model model) {
        model.addAttribute("part", new Part());
        model.addAttribute("pageTitle", "Add New Part");
        return "productmanager/part-form";
    }

    @PostMapping("/parts/add")
    public String savePart(Part part, RedirectAttributes redirectAttributes) {
        partService.save(part);
        redirectAttributes.addFlashAttribute("successMessage", "Part saved successfully!");
        return "redirect:/productmanager/parts";
    }

    @GetMapping("/parts/edit/{id}")
    public String showEditPartForm(@PathVariable Long id, Model model) {
        model.addAttribute("part", partService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid part ID")));
        model.addAttribute("pageTitle", "Edit Part");
        return "productmanager/part-form";
    }

    @GetMapping("/requests")
    public String manageRequests(Model model) {
        model.addAttribute("pendingRequests", partRequestService.findPendingRequests());
        model.addAttribute("pageTitle", "Part Requests");
        return "productmanager/manage-requests";
    }

    @GetMapping("/requests/{id}/approve")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partRequestService.approveRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part request approved and stock updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not approve request: " + e.getMessage());
        }
        return "redirect:/productmanager/requests";
    }

    @GetMapping("/requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partRequestService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not reject request: " + e.getMessage());
        }
        return "redirect:/productmanager/requests";
    }

    @GetMapping("/requests/{id}/forward")
    public String forwardRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partRequestService.forwardRequestToWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request forwarded to warehouse.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error forwarding request: " + e.getMessage());
        }
        return "redirect:/productmanager/requests";
    }
}
