package com.ailegacy.modernization.copilot.interfaces.rest.dto.performance;

import com.ailegacy.modernization.copilot.domain.enums.PerformanceIssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceFindingResponse {

    private PerformanceIssueType issueType;
    private String title;
    private String description;
    private String location;
    private String optimizationSuggestion;
    private String modernAlternative;
    private List<String> evidence;

}
