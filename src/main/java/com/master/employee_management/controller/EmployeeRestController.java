package com.master.employee_management.controller;

import com.master.employee_management.dto.EmployeeRequestDTO;
import com.master.employee_management.dto.EmployeeResponseDTO;
import com.master.employee_management.entity.Employee;
import com.master.employee_management.mapper.EmployeeMapper;
import com.master.employee_management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id){
        return ResponseEntity.ok(
                employeeMapper.toResponseDTO(employeeService.getEmployeeById(id))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDTO>> searchEmployees(@RequestParam String keyword) {
        List<EmployeeResponseDTO> results = employeeService.searchEmployees(keyword)
                .stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        // no need to check duplicate - Service handle it
        Employee saved = employeeService.saveEmployee(
                employeeMapper.toEntity(employeeRequestDTO)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeMapper.toResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        // no need to check duplicate - Service handle it
        Employee updated = employeeService.updateEmployee(
                id, employeeMapper.toEntity(employeeRequestDTO)
        );
        return ResponseEntity.ok(employeeMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
