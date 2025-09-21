package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.PurchaseOrder;
import com.bytex.customercaresystem.model.PurchaseOrderStatus;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.PurchaseOrderService;
import com.bytex.customercaresystem.service.SupplierService;
import com.bytex.customercaresystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouse")
public class WarehouseManagerController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final UserService userService;
    private final com.bytex.customercaresystem.service.PartService partService;

    public WarehouseManagerController(PurchaseOrderService purchaseOrderService, SupplierService supplierService, UserService userService, com.bytex.customercaresystem.service.PartService partService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.userService = userService;
        this.partService = partService;
    }

    private User getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @GetMapping("/dashboard")
    public String warehouseDashboard(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        model.addAttribute("pageTitle", "Warehouse Dashboard");
        return "warehouse/dashboard";
    }

    @GetMapping("/orders/new")
    public String showNewOrderForm(Model model) {
        model.addAttribute("purchaseOrder", new PurchaseOrder());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Create Purchase Order");
        return "warehouse/po-form";
    }

    @PostMapping("/orders/new")
    public String createPurchaseOrder(PurchaseOrder purchaseOrder, Authentication authentication) {
        User creator = getLoggedInUser(authentication);
        purchaseOrder.setCreatedBy(creator);
        PurchaseOrder savedOrder = purchaseOrderService.save(purchaseOrder);
        // Redirect to a details page to add items
        return "redirect:/warehouse/orders/" + savedOrder.getOrderId();
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") PurchaseOrderStatus status, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
        }
        return "redirect:/warehouse/orders/" + id;
    }

    @GetMapping("/orders/{id}")
    public String viewPoDetails(@PathVariable Long id, Model model) {
        PurchaseOrder po = purchaseOrderService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid PO Id:" + id));
        model.addAttribute("purchaseOrder", po);
        model.addAttribute("parts", partService.findAll());
        model.addAttribute("newOrderItem", new com.bytex.customercaresystem.model.OrderItem());
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("pageTitle", "Purchase Order Details");
        return "warehouse/po-details";
    }

    @PostMapping("/orders/{id}/items")
    public String addOrderItem(@PathVariable Long id, com.bytex.customercaresystem.model.OrderItem orderItem, @RequestParam("partId") Long partId, RedirectAttributes redirectAttributes) {
        try {
            com.bytex.customercaresystem.model.Part part = partService.findById(partId).orElseThrow(() -> new IllegalArgumentException("Invalid part Id:" + partId));
            orderItem.setPart(part);
            purchaseOrderService.addOrderItem(id, orderItem);
            redirectAttributes.addFlashAttribute("successMessage", "Item added to order.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + e.getMessage());
        }
        return "redirect:/warehouse/orders/" + id;
    }
}
