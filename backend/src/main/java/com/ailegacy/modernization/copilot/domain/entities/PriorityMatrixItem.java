package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single migration workstream rated by impact and effort. List order
 * (as returned by the planner) reflects priority, highest first. Embedded
 * within {@link ModernizationPlan}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityMatrixItem {

    private String item;

    private Level impact;

    private Level effort;

}
