package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.UserProfileResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link User} entities to the read-only {@link UserProfileResponse} DTO.
 */
@Component
public class UserProfileMapper {

    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
