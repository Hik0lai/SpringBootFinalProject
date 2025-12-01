package com.beehivemonitor.dto;

import com.beehivemonitor.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private User.Role role;
        private Boolean emailNotificationEnabled;

        public static UserResponse fromUser(User user) {
            return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getEmailNotificationEnabled() != null ? user.getEmailNotificationEnabled() : false
            );
        }
    }
}

