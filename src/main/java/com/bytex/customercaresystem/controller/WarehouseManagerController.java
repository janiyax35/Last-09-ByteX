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
    private final com.bytex.customercaresystem.service.PartRequestService partRequestService;

    public WarehouseManagerController(PurchaseOrderService purchaseOrderService, SupplierService supplierService, UserService userService, com.bytex.customercaresystem.service.PartService partService, com.bytex.customercaresystem.service.PartRequestService partRequestService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.userService = userService;
        this.partService = partService;
        this.partRequestService = partRequestService;
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

    @GetMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Purchase Order has been canceled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error canceling order: " + e.getMessage());
            return "redirect:/warehouse/orders/" + id;
        }
        return "redirect:/warehouse/dashboard";
    }

    // --- Supplier CRUD ---

    @GetMapping("/suppliers")
    public String manageSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Manage Suppliers");
        return "warehouse/manage-suppliers";
    }

    @GetMapping("/suppliers/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new com.bytex.customercaresystem.model.Supplier());
        model.addAttribute("pageTitle", "Add New Supplier");
        return "warehouse/supplier-form";
    }

    @PostMapping("/suppliers/add")
    public String saveSupplier(com.bytex.customercaresystem.model.Supplier supplier, RedirectAttributes redirectAttributes) {
        supplierService.save(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier saved successfully!");
        return "redirect:/warehouse/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String showEditSupplierForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid supplier Id:" + id)));
        model.addAttribute("pageTitle", "Edit Supplier");
        return "warehouse/supplier-form";
    }

    @GetMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: This supplier might be linked to existing purchase orders and cannot be deleted.");
        }
        return "redirect:/warehouse/suppliers";
    }

    @GetMapping("/stock-requests")
    public String viewStockRequests(Model model) {
        model.addAttribute("partRequests", partRequestService.findWarehousePendingRequests());
        model.addAttribute("pageTitle", "Stock Replenishment Requests");
        return "warehouse/stock-requests";
    }

    @GetMapping("/purchase-orders/new/{partRequestId}")
    public String showNewPoForRequestForm(@PathVariable Long partRequestId, Model model) {
        com.bytex.customercaresystem.model.PartRequest partRequest = partRequestService.findById(partRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Part Request ID: " + partRequestId));

        PurchaseOrder newPo = new PurchaseOrder();
        com.bytex.customercaresystem.model.OrderItem newOrderItem = new com.bytex.customercaresystem.model.OrderItem();
        newOrderItem.setPart(partRequest.getPart());
        newOrderItem.setQuantity(partRequest.getQuantity());

        model.addAttribute("partRequest", partRequest);
        model.addAttribute("purchaseOrder", newPo);
        model.addAttribute("orderItem", newOrderItem);
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("pageTitle", "Create PO for Request");
        return "warehouse/create-po-from-request";
    }

    @PostMapping("/purchase-orders/new/{partRequestId}")
    public String createPoFromRequest(@PathVariable Long partRequestId,
                                      PurchaseOrder purchaseOrder,
                                      @RequestParam("partId") Long partId,
                                      @RequestParam("quantity") int quantity,
                                      @RequestParam("unitPrice") java.math.BigDecimal unitPrice,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            User creator = getLoggedInUser(authentication);

            com.bytex.customercaresystem.model.Part part = partService.findById(partId)
                    .orElseThrow(() -> new Exception("Part not found"));

            com.bytex.customercaresystem.model.OrderItem orderItem = new com.bytex.customercaresystem.model.OrderItem();
            orderItem.setPart(part);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);

            purchaseOrderService.createPoFromRequest(partRequestId, purchaseOrder, orderItem, creator);

            redirectAttributes.addFlashAttribute("successMessage", "Purchase Order created successfully!");
            return "redirect:/warehouse/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating Purchase Order: " + e.getMessage());
            return "redirect:/warehouse/purchase-orders/new/" + partRequestId;
        }
    }
}
