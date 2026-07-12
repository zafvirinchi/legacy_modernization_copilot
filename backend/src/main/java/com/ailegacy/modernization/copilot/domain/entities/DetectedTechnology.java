package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.TechnologyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single detected technology with its confidence score and supporting evidence.
 * Embedded within {@link TechnologyDetectionResult}, not persisted independently.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectedTechnology {

    private TechnologyType technology;

    private int confidenceScore;

    private List<String> evidence;

}
