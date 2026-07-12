package com.ailegacy.modernization.copilot.interfaces.rest.dto.detection;

import com.ailegacy.modernization.copilot.domain.enums.TechnologyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectedTechnologyResponse {

    private TechnologyType technology;
    private int confidenceScore;
    private List<String> evidence;

}
