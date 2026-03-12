package com.master.employee_management.repository;

import com.master.employee_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}
