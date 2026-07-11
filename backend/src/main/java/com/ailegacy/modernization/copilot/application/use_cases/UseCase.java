package com.ailegacy.modernization.copilot.application.use_cases;

/**
 * Base use case interface following the clean architecture pattern.
 * 
 * Each use case encapsulates a single business operation with:
 * - Input request object
 * - Output response object
 * - Clear separation from other use cases
 * 
 * Usage:
 * public class CreateProjectUseCase implements UseCase<CreateProjectRequest, CreateProjectResponse> {
 *     @Override
 *     public CreateProjectResponse execute(CreateProjectRequest request) {
 *         // Implementation
 *     }
 * }
 */
public interface UseCase<Request, Response> {

    Response execute(Request request);

}
