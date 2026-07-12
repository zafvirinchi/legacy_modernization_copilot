package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single migration risk with its severity. Embedded within
 * {@link ModernizationPlan}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Risk {

    private String description;

    private Level severity;

}
