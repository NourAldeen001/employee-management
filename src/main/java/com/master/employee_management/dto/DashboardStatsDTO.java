package com.master.employee_management.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long totalEmployees;
    private long totalUsers;
    private long totalAdmins;
    private long totalDepartments;
}
