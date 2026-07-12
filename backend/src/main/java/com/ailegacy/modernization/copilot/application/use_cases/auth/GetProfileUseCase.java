package com.ailegacy.modernization.copilot.application.use_cases.auth;

import com.ailegacy.modernization.copilot.application.mappers.UserProfileMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.auth.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves the profile of the currently authenticated user.
 */
@Component
@RequiredArgsConstructor
public class GetProfileUseCase implements UseCase<String, UserProfileResponse> {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfileResponse execute(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return userProfileMapper.toProfileResponse(user);
    }

}
