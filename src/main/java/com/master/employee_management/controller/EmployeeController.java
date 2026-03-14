package com.master.employee_management.controller;

import com.master.employee_management.entity.Employee;
import com.master.employee_management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/list";
    }

    // Show Add Form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employees/form";
    }

    // Process Add Form
    @PostMapping
    public String processAddForm(@Valid @ModelAttribute Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        // Check Validation errors
        if(result.hasErrors()) {
            return "employees/form"; // send back to form with errors shown
        }
        // Check email duplicate (for create)
        if(employeeService.existsByEmail(employee.getEmail())) {
            result.rejectValue("email", "error.employee", "Email already exists");
            return "employees/form"; // send back to form with errors shown
        }
        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        return "redirect:/employees";
    }

    // Show Edit Form
    @GetMapping("{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employees/form";
    }

    // Process Edit Form
    @PostMapping("{id}/edit")
    public String processEditForm(@PathVariable Long id,
                                  @Valid @ModelAttribute Employee employee,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes){
        // Check Validation errors
        if(result.hasErrors()) {
            return "employees/form";
        }
        // Check email duplicate (for update)
        if(employeeService.existsByEmailAndIdNot(employee.getEmail(), id)) {
            result.rejectValue("email", "error.employee", "Email already exists");
            return "employees/form";
        }
        employeeService.updateEmployee(id, employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}
