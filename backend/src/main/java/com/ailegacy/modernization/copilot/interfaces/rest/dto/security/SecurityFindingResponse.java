package com.ailegacy.modernization.copilot.interfaces.rest.dto.security;

import com.ailegacy.modernization.copilot.domain.enums.SecurityIssueType;
import com.ailegacy.modernization.copilot.domain.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityFindingResponse {

    private SecurityIssueType issueType;
    private String title;
    private String description;
    private Severity severity;
    private int riskScore;
    private String location;
    private String recommendation;
    private String modernAlternative;
    private List<String> evidence;

}
