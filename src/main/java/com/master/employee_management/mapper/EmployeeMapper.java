package com.master.employee_management.mapper;

import com.master.employee_management.dto.EmployeeRequestDTO;
import com.master.employee_management.dto.EmployeeResponseDTO;
import com.master.employee_management.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    /// Entity -> RequestDTO to pre-fill the form
    public EmployeeRequestDTO toRequestDTO(Employee employee) {
        return EmployeeRequestDTO.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .build();
    }

    /// Entity -> ResponseDTO (for sending data out)
    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .build();
    }

    /// RequestDTO -> Entity (for saving incoming data)
    public Employee toEntity(EmployeeRequestDTO dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .salary(dto.getSalary())
                .build();
    }
}
