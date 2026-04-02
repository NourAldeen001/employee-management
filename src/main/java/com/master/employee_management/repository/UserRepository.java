package com.master.employee_management.repository;

import com.master.employee_management.entity.Role;
import com.master.employee_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole(Role role);
    boolean existsByUsername(String username);
    long countByRoleAndEnabled(Role role, boolean enabled);
    @Query("SELECT u FROM User u WHERE u.username != :username")
    List<User> findAllExceptCurrentUser(@Param("username") String username);
}
