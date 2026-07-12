package com.ailegacy.modernization.copilot.application.use_cases.project;

import org.springframework.web.multipart.MultipartFile;

/**
 * Input for {@link UploadProjectUseCase}.
 */
public record UploadProjectCommand(MultipartFile file, String ownerId) {
}
