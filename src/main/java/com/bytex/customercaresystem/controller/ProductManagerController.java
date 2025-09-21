package com.bytex.customercaresystem.controller;

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
        model.addAttribute("lowStockParts", partService.findLowStockParts());
        model.addAttribute("pendingRequests", partRequestService.findPendingRequests());
        model.addAttribute("pageTitle", "Product Manager Dashboard");
        return "productmanager/dashboard";
    }

    @GetMapping("/requests/{id}/approve")
    public String approveRequest(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            partRequestService.approveRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part request approved and stock updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving request: " + e.getMessage());
        }
        return "redirect:/productmanager/dashboard";
    }

    @GetMapping("/requests/{id}/reject")
    public String rejectRequest(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            partRequestService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part request has been rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + e.getMessage());
        }
        return "redirect:/productmanager/dashboard";
    }

    @GetMapping("/parts")
    public String manageParts(Model model) {
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("pageTitle", "Manage Parts Inventory");
        return "productmanager/manage-parts";
    }

    @GetMapping("/parts/add")
    public String showAddPartForm(Model model) {
        model.addAttribute("part", new com.bytex.customercaresystem.model.Part());
        model.addAttribute("pageTitle", "Add New Part");
        return "productmanager/part-form";
    }

    @PostMapping("/parts/add")
    public String savePart(com.bytex.customercaresystem.model.Part part, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        partService.save(part);
        redirectAttributes.addFlashAttribute("successMessage", "Part saved successfully!");
        return "redirect:/productmanager/parts";
    }

    @GetMapping("/parts/edit/{id}")
    public String showEditPartForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        com.bytex.customercaresystem.model.Part part = partService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + id));
        model.addAttribute("part", part);
        model.addAttribute("pageTitle", "Edit Part");
        return "productmanager/part-form";
    }

    // The POST for edit will be handled by the same savePart method, as it has the ID

    @GetMapping("/parts/delete/{id}")
    public String discontinuePart(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            partService.discontinuePart(id);
            redirectAttributes.addFlashAttribute("successMessage", "Part has been discontinued.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/productmanager/parts";
    }
}
