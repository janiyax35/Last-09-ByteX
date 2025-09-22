package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.OrderItem;
import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PurchaseOrder;
import com.bytex.customercaresystem.model.PurchaseOrderStatus;
import com.bytex.customercaresystem.model.Supplier;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/warehouse")
public class WarehouseManagerController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final UserService userService;
    private final PartService partService;
    private final PartRequestService partRequestService;

    public WarehouseManagerController(PurchaseOrderService purchaseOrderService, SupplierService supplierService, UserService userService, PartService partService, PartRequestService partRequestService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.userService = userService;
        this.partService = partService;
        this.partRequestService = partRequestService;
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @GetMapping("/dashboard")
    public String warehouseDashboard(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "warehouse/dashboard";
    }

    @GetMapping("/stock-requests")
    public String viewStockRequests(Model model) {
        model.addAttribute("partRequests", partRequestService.findWarehousePendingRequests());
        return "warehouse/stock-requests";
    }

    @GetMapping("/requests/{id}/fulfill")
    public String fulfillRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partRequestService.fulfillRequestFromWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request fulfilled from stock.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/warehouse/stock-requests";
    }

    @GetMapping("/purchase-orders/new/{partRequestId}")
    public String showNewPoForRequestForm(@PathVariable Long partRequestId, Model model) {
        com.bytex.customercaresystem.model.PartRequest partRequest = partRequestService.findById(partRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Part Request ID: " + partRequestId));

        PurchaseOrder newPo = new PurchaseOrder();
        model.addAttribute("partRequest", partRequest);
        model.addAttribute("purchaseOrder", newPo);
        model.addAttribute("suppliers", supplierService.findByParts(partRequest.getPart()));
        return "warehouse/po-form";
    }

    @PostMapping("/purchase-orders/new/{partRequestId}")
    public String createPoFromRequest(@PathVariable Long partRequestId, PurchaseOrder purchaseOrder, @RequestParam Long supplierId, @RequestParam BigDecimal unitPrice, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            User creator = getLoggedInUser(authentication);
            com.bytex.customercaresystem.model.PartRequest partRequest = partRequestService.findById(partRequestId).orElseThrow(() -> new Exception("Part Request not found"));
            Supplier supplier = supplierService.findById(supplierId).orElseThrow(() -> new Exception("Supplier not found"));

            purchaseOrder.setSupplier(supplier);

            OrderItem orderItem = new OrderItem();
            orderItem.setPart(partRequest.getPart());
            orderItem.setQuantity(partRequest.getQuantity());
            orderItem.setUnitPrice(unitPrice);

            purchaseOrderService.createPoFromRequest(partRequestId, purchaseOrder, orderItem, creator);

            redirectAttributes.addFlashAttribute("successMessage", "Purchase Order created successfully!");
            return "redirect:/warehouse/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating Purchase Order: " + e.getMessage());
            return "redirect:/warehouse/stock-requests";
        }
    }

    @GetMapping("/orders/{id}")
    public String viewPoDetails(@PathVariable Long id, Model model) {
        PurchaseOrder po = purchaseOrderService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid PO ID"));
        model.addAttribute("purchaseOrder", po);
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        return "warehouse/po-details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam PurchaseOrderStatus status, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/warehouse/orders/" + id;
    }

    @GetMapping("/suppliers")
    public String manageSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        return "warehouse/manage-suppliers";
    }

    @GetMapping("/suppliers/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("allParts", partService.findAll());
        return "warehouse/supplier-form";
    }

    @PostMapping("/suppliers/add")
    public String saveSupplier(Supplier supplier, RedirectAttributes redirectAttributes) {
        try {
            if (supplier.getSupplierId() == null) {
                supplierService.save(supplier);
                redirectAttributes.addFlashAttribute("successMessage", "Supplier added successfully!");
            } else {
                supplierService.updateSupplier(supplier);
                redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving supplier: " + e.getMessage());
        }
        return "redirect:/warehouse/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String showEditSupplierForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid supplier ID")));
        model.addAttribute("allParts", partService.findAll());
        return "warehouse/supplier-form";
    }
}
