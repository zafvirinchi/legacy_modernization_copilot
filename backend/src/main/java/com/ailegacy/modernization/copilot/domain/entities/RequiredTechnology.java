package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.ModernTechnology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Whether one of the fixed modern-stack technologies is recommended for this
 * project's migration, and why. Embedded within {@link ModernizationPlan}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredTechnology {

    private ModernTechnology technology;

    private boolean recommended;

    private String reason;

}
