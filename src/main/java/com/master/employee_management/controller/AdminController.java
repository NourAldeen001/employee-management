package com.master.employee_management.controller;

import com.master.employee_management.dto.UserRequestDTO;
import com.master.employee_management.entity.Role;
import com.master.employee_management.entity.User;
import com.master.employee_management.exception.DuplicateEmailException;
import com.master.employee_management.exception.EmployeeNotFoundException;
import com.master.employee_management.exception.InvalidOperationException;
import com.master.employee_management.mapper.UserMapper;
import com.master.employee_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", userService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsersExceptCurrent(getCurrentUserUsername()));
        return "admin/users/list";
    }

    // Show Add Form
    @GetMapping("/users/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserRequestDTO());
        model.addAttribute("roles", Role.values());
        return "admin/users/form";
    }

    // Process Add Form
    @PostMapping("/users")
    public String processCreateForm(@Valid @ModelAttribute("user") UserRequestDTO userRequestDTO,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/users/form";
        }

        try {
            userService.createUser(userMapper.toEntity(userRequestDTO));
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/admin/users";
        }
        catch (DuplicateEmailException ex) {
            result.rejectValue("username", "error.user", ex.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/users/form";
        }
    }

    // Show Edit Role Form
    @GetMapping("/users/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", userService.getUserById(id));
            model.addAttribute("roles", Role.values());
            return "admin/users/edit";
        }
        catch(EmployeeNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    // Process Edit Role Form
    @PostMapping("/users/{id}/edit")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam Role role,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully!");
        }
        catch(EmployeeNotFoundException | InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Toggle Enabled / Disabled
    @GetMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUerEnabled(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + (user.isEnabled() ? "enabled" : "disabled") + " successfully!");
        }
        catch(EmployeeNotFoundException | InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        }
        catch(EmployeeNotFoundException | InvalidOperationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    ///  Helper Method
    private String getCurrentUserUsername() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

}
