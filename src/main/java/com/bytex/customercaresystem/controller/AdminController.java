package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final TicketService ticketService;
    private final PurchaseOrderService purchaseOrderService;
    private final ActivityLogService activityLogService;

    public AdminController(UserService userService, TicketService ticketService, PurchaseOrderService purchaseOrderService, ActivityLogService activityLogService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.purchaseOrderService = purchaseOrderService;
        this.activityLogService = activityLogService;
    }

    private User getLoggedInUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.findAllUsers().size());
        model.addAttribute("ticketCount", ticketService.findAllTickets().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/manage-users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/add")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID")));
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, User user, Authentication authentication, RedirectAttributes redirectAttributes) {
        User loggedInUser = getLoggedInUser(authentication);
        if (loggedInUser.getUserId().equals(id) && user.getRole() != Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change your own role.");
            return "redirect:/admin/users/edit/" + id;
        }
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User loggedInUser = getLoggedInUser(authentication);
        if (loggedInUser.getUserId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/tickets")
    public String monitorTickets(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("tickets", ticketService.searchTickets(keyword, null, null));
        model.addAttribute("keyword", keyword);
        return "admin/all-tickets";
    }

    @PostMapping("/tickets/{id}/assign-staff")
    public String assignStaffToTicket(@PathVariable Long id, @RequestParam Long staffId, RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID"));
        User staffMember = userService.findById(staffId).orElseThrow(() -> new IllegalArgumentException("Invalid staff ID"));
        ticketService.acceptTicket(ticket, staffMember);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket assigned to " + staffMember.getFullName());
        return "redirect:/staff/tickets/" + id;
    }

    @GetMapping("/parts-orders")
    public String monitorPartsOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "admin/parts-orders";
    }

    @GetMapping("/logs")
    public String viewActivityLogs(Model model) {
        model.addAttribute("logs", activityLogService.findAllLogs());
        return "admin/activity-logs";
    }
}
