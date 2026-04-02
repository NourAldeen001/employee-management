package com.master.employee_management.mapper;

import com.master.employee_management.dto.UserRequestDTO;
import com.master.employee_management.dto.UserResponseDTO;
import com.master.employee_management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /// Entity -> ResponseDTO (for sending data out "Display user")
    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

    ///  RequestDTO -> Entity (for saving incoming data "Create")
    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole())
                .enabled(true)
                .build();
    }

}
