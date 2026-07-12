package com.ailegacy.modernization.copilot.interfaces.rest.dto.planner;

import com.ailegacy.modernization.copilot.domain.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityMatrixItemResponse {

    private String item;
    private Level impact;
    private Level effort;

}
