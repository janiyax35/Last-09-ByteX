package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.*;
import com.bytex.customercaresystem.models.enums.PartRequestStatus;
import com.bytex.customercaresystem.models.enums.PurchaseOrderStatus;
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
@RequestMapping("/warehouse-manager")
public class WarehouseManagerController {

    @Autowired
    private PartRequestService partRequestService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private PartService partService;
    @Autowired
    private UserService userService;

    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @GetMapping("/dashboard")
    public String wmDashboard(Model model) {
        // Find requests approved by PM but needing purchase
        List<PartRequest> requestsToPurchase = partRequestService.findAll().stream()
                .filter(pr -> pr.getStatus() == PartRequestStatus.APPROVED)
                .collect(Collectors.toList());

        List<PurchaseOrder> activePOs = purchaseOrderService.findAll().stream()
                .filter(po -> po.getStatus() != PurchaseOrderStatus.DELIVERED && po.getStatus() != PurchaseOrderStatus.CANCELLED)
                .collect(Collectors.toList());

        model.addAttribute("requestsToPurchase", requestsToPurchase);
        model.addAttribute("activePOs", activePOs);
        return "warehouse-manager/dashboard";
    }

    // Supplier CRUD
    @GetMapping("/suppliers")
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        return "warehouse-manager/suppliers";
    }

    @GetMapping("/suppliers/new")
    public String newSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "warehouse-manager/supplier-form";
    }

    @PostMapping("/suppliers/new")
    public String saveSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        supplierService.save(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier created successfully.");
        return "redirect:/warehouse-manager/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id).orElseThrow());
        return "warehouse-manager/supplier-form";
    }

    @PostMapping("/suppliers/edit/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        supplier.setId(id);
        supplierService.save(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully.");
        return "redirect:/warehouse-manager/suppliers";
    }

    // Purchase Order Management
    @GetMapping("/purchase-orders")
    public String listPurchaseOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "warehouse-manager/purchase-orders";
    }

    @GetMapping("/purchase-orders/new")
    public String newPurchaseOrderForm(Model model) {
        PurchaseOrder po = new PurchaseOrder();
        po.getOrderItems().add(new OrderItem()); // Add one empty item line

        model.addAttribute("purchaseOrder", po);
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("parts", partService.findAllParts());
        return "warehouse-manager/po-form";
    }

    @PostMapping("/purchase-orders/new")
    public String savePurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder, Principal principal, RedirectAttributes redirectAttributes) {
        User warehouseManager = getCurrentUser(principal);
        purchaseOrder.setCreatedBy(warehouseManager);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);

        // Link items back to the order
        for (OrderItem item : purchaseOrder.getOrderItems()) {
            item.setPurchaseOrder(purchaseOrder);
        }

        purchaseOrderService.createPurchaseOrder(purchaseOrder);
        redirectAttributes.addFlashAttribute("successMessage", "Purchase Order created successfully.");
        return "redirect:/warehouse-manager/purchase-orders";
    }

    @PostMapping("/purchase-orders/update-status/{id}")
    public String updatePoStatus(@PathVariable Long id, @RequestParam PurchaseOrderStatus status, RedirectAttributes redirectAttributes) {
        PurchaseOrder po = purchaseOrderService.findById(id).orElseThrow();
        po.setStatus(status);
        purchaseOrderService.save(po);

        // If delivered, update stock
        if (status == PurchaseOrderStatus.DELIVERED) {
            for (OrderItem item : po.getOrderItems()) {
                Part part = item.getPart();
                part.setCurrentStock(part.getCurrentStock() + item.getQuantity());
                partService.savePart(part);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Order marked as DELIVERED and stock has been updated.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        }

        return "redirect:/warehouse-manager/purchase-orders";
    }
}