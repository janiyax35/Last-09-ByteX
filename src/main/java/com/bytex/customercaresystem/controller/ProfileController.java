package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in user"));

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "My Profile");
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(User user, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User loggedInUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in user"));

        try {
            // Use the new, more secure method for profile updates
            userService.updateUserProfile(loggedInUser.getUserId(), user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
