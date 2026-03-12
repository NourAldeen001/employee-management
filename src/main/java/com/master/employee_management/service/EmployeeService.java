package com.master.employee_management.service;

import com.master.employee_management.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}
