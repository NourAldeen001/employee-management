package com.master.employee_management.service;

import com.master.employee_management.entity.Employee;
import com.master.employee_management.exception.DuplicateEmailException;
import com.master.employee_management.exception.EmployeeNotFoundException;
import com.master.employee_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    ///  business logic belongs in the Service
    ///  Owns the rule, throws exception
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        if(employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);
        if(employeeRepository.existsByEmailAndIdNot(employee.getEmail(), id)) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setDepartment(employee.getDepartment());
        existing.setSalary(employee.getSalary());
        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> searchEmployees(String keyword) {
        if(keyword == null || keyword.trim().isEmpty()) {
            return employeeRepository.findAll();
        }
        return employeeRepository.search(keyword.trim());
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return employeeRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }
}
