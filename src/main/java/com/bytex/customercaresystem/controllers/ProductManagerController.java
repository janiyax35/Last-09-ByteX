package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.Part;
import com.bytex.customercaresystem.models.PartRequest;
import com.bytex.customercaresystem.models.Ticket;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.models.enums.PartRequestStatus;
import com.bytex.customercaresystem.models.enums.PartStatus;
import com.bytex.customercaresystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product-manager")
public class ProductManagerController {

    @Autowired
    private PartService partService;
    @Autowired
    private PartRequestService partRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @GetMapping("/dashboard")
    public String pmDashboard(Model model) {
        List<Part> allParts = partService.findAllParts();
        List<PartRequest> pendingRequests = partRequestService.findAll().stream()
                .filter(pr -> pr.getStatus() == PartRequestStatus.PENDING)
                .collect(Collectors.toList());

        model.addAttribute("lowStockParts", allParts.stream().filter(p -> p.getStatus() == PartStatus.LOW_STOCK || p.getStatus() == PartStatus.OUT_OF_STOCK).count());
        model.addAttribute("pendingRequestsCount", pendingRequests.size());
        return "product-manager/dashboard";
    }

    // Parts CRUD
    @GetMapping("/parts")
    public String listParts(Model model) {
        model.addAttribute("parts", partService.findAllParts());
        return "product-manager/parts";
    }

    @GetMapping("/parts/new")
    public String newPartForm(Model model) {
        model.addAttribute("part", new Part());
        return "product-manager/part-form";
    }

    @PostMapping("/parts/new")
    public String savePart(@ModelAttribute Part part, RedirectAttributes redirectAttributes) {
        partService.savePart(part);
        redirectAttributes.addFlashAttribute("successMessage", "Part created successfully!");
        return "redirect:/product-manager/parts";
    }

    @GetMapping("/parts/edit/{id}")
    public String editPartForm(@PathVariable Long id, Model model) {
        Part part = partService.findPartById(id).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + id));
        model.addAttribute("part", part);
        return "product-manager/part-form";
    }

    @PostMapping("/parts/edit/{id}")
    public String updatePart(@PathVariable Long id, @ModelAttribute Part part, RedirectAttributes redirectAttributes) {
        part.setId(id);
        partService.savePart(part);
        redirectAttributes.addFlashAttribute("successMessage", "Part updated successfully!");
        return "redirect:/product-manager/parts";
    }

    @GetMapping("/parts/delete/{id}")
    public String deletePart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Business Rule: Don't delete, mark as discontinued.
        Part part = partService.findPartById(id).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + id));
        part.setStatus(PartStatus.DISCONTINUED);
        partService.savePart(part);
        redirectAttributes.addFlashAttribute("successMessage", "Part marked as discontinued.");
        return "redirect:/product-manager/parts";
    }

    // Part Requests Management
    @GetMapping("/requests")
    public String listPartRequests(Model model) {
        model.addAttribute("requests", partRequestService.findAll());
        return "product-manager/requests";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        PartRequest request = partRequestService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        Part part = request.getPart();

        if (part.getCurrentStock() >= request.getQuantity()) {
            // Fulfill the request
            part.setCurrentStock(part.getCurrentStock() - request.getQuantity());
            partService.savePart(part);

            request.setStatus(PartRequestStatus.FULFILLED);
            partRequestService.save(request);

            updateTicketStageAfterPartFulfilled(request);

            redirectAttributes.addFlashAttribute("successMessage", "Request approved and fulfilled from stock.");
        } else {
            // Not enough stock, escalate to Warehouse
            request.setStatus(PartRequestStatus.APPROVED); // Approved but pending purchase
            partRequestService.save(request);

            redirectAttributes.addFlashAttribute("infoMessage", "Request approved, but stock is insufficient. A purchase order is now required.");
        }
        return "redirect:/product-manager/requests";
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        PartRequest request = partRequestService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        request.setStatus(PartRequestStatus.REJECTED);
        partRequestService.save(request);
        redirectAttributes.addFlashAttribute("successMessage", "Request has been rejected.");
        return "redirect:/product-manager/requests";
    }

    private void updateTicketStageAfterPartFulfilled(PartRequest request) {
        User technician = request.getRequestor();
        List<Ticket> tickets = ticketService.findAllTickets().stream()
                .filter(t -> "AWAITING_PARTS".equals(t.getStage()) &&
                             t.getRepairs().stream().anyMatch(r -> r.getTechnician().equals(technician)))
                .collect(Collectors.toList());

        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.get(0);
            ticket.setStage("REPAIR_IN_PROGRESS");
            ticketService.saveTicket(ticket);
        }
    }
}