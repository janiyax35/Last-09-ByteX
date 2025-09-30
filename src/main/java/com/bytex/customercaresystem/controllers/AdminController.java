package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.models.enums.UserRole;
import com.bytex.customercaresystem.services.UserService;
import com.bytex.customercaresystem.services.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        // In a real scenario, you would add more statistics here
        return "admin/dashboard";
    }

    // User Management CRUD
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values()); // Provide all roles to the form
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String saveUser(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username is already taken");
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Email is already registered");
        }

        if (result.hasErrors()) {
            return "admin/user-form";
        }

        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        return "admin/user-form";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Add roles back to the model if there are errors, so the form can be re-rendered
            redirectAttributes.addFlashAttribute("roles", UserRole.values());
            return "admin/user-form";
        }
        user.setId(id); // Ensure the ID is set for the update
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    // Activity Logs
    @GetMapping("/logs")
    public String viewActivityLogs(Model model) {
        model.addAttribute("logs", activityLogService.findAllLogs());
        return "admin/activity-logs";
    }
}