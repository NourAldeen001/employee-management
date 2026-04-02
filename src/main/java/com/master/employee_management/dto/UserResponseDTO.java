package com.master.employee_management.dto;

import com.master.employee_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponseDTO {

    private Long id;
    private String username;
    private Role role;
    private boolean enabled;
}
