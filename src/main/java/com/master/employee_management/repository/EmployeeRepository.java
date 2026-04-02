package com.master.employee_management.repository;

import com.master.employee_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT COUNT(DISTINCT emp.department) FROM Employee emp")
    long countDistinctDepartments();
    @Query("SELECT emp FROM Employee emp WHERE " +
            "LOWER(emp.firstName) like LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(emp.lastName) like LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(emp.email) like LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(emp.department) like LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> search(@Param("keyword") String keyword);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}
