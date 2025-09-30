package com.bytex.customercaresystem.controllers;

import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String viewSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        // Server-side validation
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username is already taken");
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Email is already registered");
        }

        if (result.hasErrors()) {
            return "signup";
        }

        userService.saveCustomer(user);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}