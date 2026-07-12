package com.ailegacy.modernization.copilot.interfaces.rest.dto.auth;

import com.ailegacy.modernization.copilot.domain.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String id;
    private String name;
    private String email;
    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
