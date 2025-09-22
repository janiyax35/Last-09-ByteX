package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.ActivityLogService;
import com.bytex.customercaresystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final com.bytex.customercaresystem.service.TicketService ticketService;

    public AdminController(UserService userService, ActivityLogService activityLogService, com.bytex.customercaresystem.service.TicketService ticketService) {
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.ticketService = ticketService;
    }

    private User getLoggedInUser(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.findAllUsers();
        long customerCount = users.stream().filter(u -> u.getRole() == com.bytex.customercaresystem.model.Role.CUSTOMER).count();
        long staffCount = users.stream().filter(u -> u.getRole() == com.bytex.customercaresystem.model.Role.STAFF).count();
        long techCount = users.stream().filter(u -> u.getRole() == com.bytex.customercaresystem.model.Role.TECHNICIAN).count();

        model.addAttribute("userCount", users.size());
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("staffCount", staffCount);
        model.addAttribute("techCount", techCount);

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
        model.addAttribute("pageTitle", "Add New User");
        // Admins can create any role
        model.addAttribute("roles", com.bytex.customercaresystem.model.Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/add")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        try {
            // A more robust service method might be needed to check for uniqueness beforehand
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Could not create user. Username or Email might already exist.");
            // In case of error, redirect back to the form with the entered data
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin/users/add";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@org.springframework.web.bind.annotation.PathVariable Long id, RedirectAttributes redirectAttributes, org.springframework.security.core.Authentication authentication) {
        User loggedInUser = getLoggedInUser(authentication);
        if (loggedInUser.getUserId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: You cannot delete your own account.");
            return "redirect:/admin/users";
        }

        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("roles", com.bytex.customercaresystem.model.Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@org.springframework.web.bind.annotation.PathVariable Long id, User user, RedirectAttributes redirectAttributes, org.springframework.security.core.Authentication authentication) {
        try {
            User loggedInUser = getLoggedInUser(authentication);
            if (loggedInUser.getUserId().equals(id) && user.getRole() != loggedInUser.getRole()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: You cannot change your own role.");
                return "redirect:/admin/users/edit/" + id;
            }

            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/logs")
    public String viewActivityLogs(Model model) {
        model.addAttribute("logs", activityLogService.findAllLogs());
        return "admin/activity-logs";
    }

    @GetMapping("/tickets")
    public String monitorTickets(Model model, @RequestParam(required = false) String keyword) {
        List<com.bytex.customercaresystem.model.Ticket> tickets;
        if (keyword != null && !keyword.isEmpty()) {
            tickets = ticketService.searchTickets(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            tickets = ticketService.findAllTickets();
        }
        model.addAttribute("tickets", tickets);
        model.addAttribute("pageTitle", "All System Tickets");
        return "admin/all-tickets";
    }
}
