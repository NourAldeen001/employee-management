package com.master.employee_management.controller;

import com.master.employee_management.dto.EmployeeRequestDTO;
import com.master.employee_management.entity.Employee;
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
    public String processAddForm(@Valid @ModelAttribute EmployeeRequestDTO employeeRequestDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        // Check Validation errors
        if(result.hasErrors()) {
            return "employees/form"; // send back to form with errors shown
        }
        // Check email duplicate (for create)
        if(employeeService.existsByEmail(employeeRequestDTO.getEmail())) {
            result.rejectValue("email", "error.employee", "Email already exists");
            return "employees/form"; // send back to form with errors shown
        }
        employeeService.saveEmployee(employeeMapper.toEntity(employeeRequestDTO));
        redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        return "redirect:/employees";
    }

    // Show Edit Form
    @GetMapping("{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        // Map Entity -> RequestDTO to pre-fill the form
        EmployeeRequestDTO employeeRequestDTO = EmployeeRequestDTO.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .build();
        model.addAttribute("employee", employeeRequestDTO);
        model.addAttribute("employeeId", id); // pass id for form action
        return "employees/form";
    }

    // Process Edit Form
    @PostMapping("{id}/edit")
    public String processEditForm(@PathVariable Long id,
                                  @Valid @ModelAttribute EmployeeRequestDTO employeeRequestDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes){
        // Check Validation errors
        if(result.hasErrors()) {
            return "employees/form";
        }
        // Check email duplicate (for update)
        if(employeeService.existsByEmailAndIdNot(employeeRequestDTO.getEmail(), id)) {
            result.rejectValue("email", "error.employee", "Email already exists");
            return "employees/form";
        }
        employeeService.updateEmployee(id, employeeMapper.toEntity(employeeRequestDTO));
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
