package com.master.employee_management.service;

import com.master.employee_management.dto.DashboardStatsDTO;
import com.master.employee_management.entity.Role;
import com.master.employee_management.entity.User;
import com.master.employee_management.exception.DuplicateEmailException;
import com.master.employee_management.exception.EmployeeNotFoundException;
import com.master.employee_management.exception.InvalidOperationException;
import com.master.employee_management.repository.EmployeeRepository;
import com.master.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsersExceptCurrent(String currentUsername) {
        return userRepository.findAllExceptCurrentUser(currentUsername);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    public User createUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateEmailException(user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUserRole(Long id, Role newRole) {
        User user = getUserById(id);

        // Cannot edit yourself
        String currentUsername = getCurrentUserUsername();
        if(user.getUsername().equals(currentUsername)) {
            throw new InvalidOperationException(
                    "Cannot change your own role");
        }

        // Must keep at least one Admin
        if(user.getRole() == Role.ADMIN && newRole == Role.USER) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if(adminCount <= 1) {
                throw new InvalidOperationException(
                        "Cannot demote the last Admin in the system");
            }
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Override
    public User toggleUerEnabled(Long id) {
        User user = getUserById(id);

        // Cannot disable yourself
        String currentUsername = getCurrentUserUsername();
        if(user.getUsername().equals(currentUsername)) {
            throw new InvalidOperationException(
                    "Cannot disable your own account");
        }

        // Cannot disable last Admin
        if(user.getRole() == Role.ADMIN && user.isEnabled()) {
            long activeAdminCount = userRepository.countByRoleAndEnabled(Role.ADMIN, true);
            if(activeAdminCount <= 1) {
                throw new InvalidOperationException(
                        "Cannot disable last active Admin");
            }
        }

        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);

        // Cannot delete yourself
        String currentUsername = getCurrentUserUsername();
        if(user.getUsername().equals(currentUsername)) {
            throw new InvalidOperationException(
                    "Cannot delete your own account");
        }

        // Cannot delete last Admin
        if(user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if(adminCount <= 1) {
                throw new InvalidOperationException(
                        "Cannot delete last admin in the system");
            }
        }

        userRepository.delete(user);
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                .totalEmployees(employeeRepository.count())
                .totalUsers(userRepository.count())
                .totalAdmins(userRepository.countByRole(Role.ADMIN))
                .totalDepartments(employeeRepository.countDistinctDepartments())
                .build();
    }

    /// Helper Method
    private String getCurrentUserUsername() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }
}
