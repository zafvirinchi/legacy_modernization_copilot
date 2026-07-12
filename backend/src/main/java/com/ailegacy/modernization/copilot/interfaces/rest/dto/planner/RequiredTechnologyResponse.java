package com.ailegacy.modernization.copilot.interfaces.rest.dto.planner;

import com.ailegacy.modernization.copilot.domain.enums.ModernTechnology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredTechnologyResponse {

    private ModernTechnology technology;
    private boolean recommended;
    private String reason;

}
