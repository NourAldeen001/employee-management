package com.master.employee_management.service;

import com.master.employee_management.dto.DashboardStatsDTO;
import com.master.employee_management.entity.Role;
import com.master.employee_management.entity.User;

import java.util.List;

public interface UserService {

    // List<User> getAllUsers();
    List<User> getAllUsersExceptCurrent(String currentUsername);
    User getUserById(Long id);
    User createUser(User user);
    User updateUserRole(Long id, Role newRole);
    User toggleUerEnabled(Long id);
    void deleteUser(Long id);
    DashboardStatsDTO getDashboardStats();

}
