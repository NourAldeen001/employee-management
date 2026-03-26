package com.master.employee_management.controller;

import com.master.employee_management.dto.EmployeeRequestDTO;
import com.master.employee_management.entity.Employee;
import com.master.employee_management.exception.DuplicateEmailException;
import com.master.employee_management.exception.EmployeeNotFoundException;
import com.master.employee_management.mapper.EmployeeMapper;
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
    private final EmployeeMapper employeeMapper;

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String keyword,
                                Model model) {
        model.addAttribute("employees", employeeService.searchEmployees(keyword));
        model.addAttribute("keyword", keyword);
        return "employees/list";
    }

    // Show Add Form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDTO());
        return "employees/form";
    }

    // Process Add Form
    @PostMapping
    public String processAddForm(@Valid @ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        // Check Validation errors
        if(result.hasErrors()) {
            return "employees/form"; // send back to form with errors shown
        }
        // Check email duplicate (for create)
        try {
            employeeService.saveEmployee(employeeMapper.toEntity(employeeRequestDTO));
            redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
            return "redirect:/employees";
        }
        catch(DuplicateEmailException ex) {
            result.rejectValue("email", "error.employee", ex.getMessage());
            return "employees/form"; // send back to form with errors shown
        }
    }

    // Show Edit Form
    @GetMapping("{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            model.addAttribute("employee", employeeMapper.toRequestDTO(employee));
            model.addAttribute("employeeId", id); // Edit mode signal
            return "employees/form";
        }
        catch(EmployeeNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/employees";
        }
    }

    // Process Edit Form
    @PostMapping("{id}/edit")
    public String processEditForm(@PathVariable Long id,
                                  @Valid @ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes){
        // Check Validation errors
        if(result.hasErrors()) {
            model.addAttribute("employeeId", id);
            return "employees/form";
        }
        // Check email duplicate (for update)
        try {
            employeeService.updateEmployee(id, employeeMapper.toEntity(employeeRequestDTO));
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
            return "redirect:/employees";
        }
        catch(DuplicateEmailException ex) {
            result.rejectValue("email", "error.employee", ex.getMessage());
            model.addAttribute("employeeId", id);
            return "employees/form";
        }
        catch(EmployeeNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        }
        catch(EmployeeNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/employees";
    }
}
