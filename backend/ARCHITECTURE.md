# Backend Folder Structure

## Overview

The backend follows **Clean Architecture** principles with strict separation of concerns across layers:

### Layer Organization

```
src/main/java/com/ailegacy/modernization/copilot/
├── domain/                          # Business logic (Framework-independent)
│   ├── models/                      # Value objects and aggregates
│   ├── entities/                    # Domain entities
│   ├── enums/                       # Domain enums
│   ├── exceptions/                  # Domain exceptions
│   ├── repositories/                # Repository interfaces (contracts)
│   └── services/                    # Domain services (business logic)
│
├── application/                     # Use cases and orchestration
│   ├── use_cases/                   # Use case implementations
│   ├── orchestrators/               # Workflow orchestrators
│   ├── services/                    # Application services
│   ├── dto/                         # Application DTOs
│   └── mappers/                     # Entity-to-DTO mappers
│
├── interfaces/                      # External communication
│   └── rest/                        # REST API layer
│       ├── controllers/             # REST endpoints
│       ├── dto/                     # API request/response DTOs
│       └── exception/               # REST exception handlers
│
├── infrastructure/                  # Technical implementations
│   ├── persistence/                 # MongoDB repositories
│   │   └── repositories/            # Spring Data Repository implementations
│   ├── ai/                          # LangChain4j and AI integrations
│   ├── analysis/                    # Legacy code analysis engines
│   ├── storage/                     # File and object storage
│   ├── security/                    # JWT, authentication, authorization
│   ├── logging/                     # Audit and structured logging
│   ├── queue/                       # Job queue implementations
│   ├── config/                      # Spring configurations
│   └── utils/                       # Common utilities

src/main/resources/
├── application.yml                  # Main configuration
├── application-dev.yml              # Development configuration
└── application-prod.yml             # Production configuration

src/test/
└── java/                            # Unit and integration tests
```

## Architectural Principles

### 1. **Domain Layer** (Business Logic)
- **Independent** of frameworks and external libraries
- Contains business rules and validation logic
- Defines repository and service interfaces
- Throws domain-specific exceptions

**Key packages**:
- `domain.models`: Value objects, aggregates
- `domain.entities`: Core business entities
- `domain.exceptions`: `DomainException`, `BusinessLogicException`, `ValidationException`, `UnauthorizedException`
- `domain.repositories`: Repository interfaces (contracts)
- `domain.services`: Domain services encapsulating complex business logic

### 2. **Application Layer** (Use Cases)
- Orchestrates domain logic and infrastructure
- Implements use cases (application workflows)
- Manages transactions and state
- Converts between DTOs and domain models

**Key packages**:
- `application.use_cases`: UseCase<Request, Response> implementations
- `application.orchestrators`: Complex workflow coordination
- `application.services`: Application-level business logic
- `application.mappers`: DTO-to-Entity mappers (MapStruct)

### 3. **Interfaces Layer** (Port Adapters)
- REST controllers expose API endpoints
- Handles request/response conversion
- Validates incoming requests
- Returns standardized JSON responses

**Key packages**:
- `interfaces.rest.controllers`: Spring @RestController classes
- `interfaces.rest.dto`: Request/Response DTOs
- `interfaces.rest.exception`: @ControllerAdvice global exception handler

### 4. **Infrastructure Layer** (External Systems)
- MongoDB persistence (Spring Data)
- JWT authentication and authorization
- LangChain4j AI integration
- File upload and storage
- Logging and auditing
- Job queue management

**Key packages**:
- `infrastructure.persistence`: MongoRepository implementations
- `infrastructure.ai`: LangChain4j configuration, AI agents
- `infrastructure.analysis`: Legacy code analyzers, rule engines
- `infrastructure.security`: JWT providers, security filters
- `infrastructure.config`: Spring configurations (Security, OpenAPI, MongoDB)
- `infrastructure.utils`: File handling, string utilities, validation

## Technology Stack

### Core
- **Java 21** with Spring Boot 3.3+
- **Maven** for build and dependency management
- **Spring Data MongoDB** for persistence
- **Spring Security** with JWT

### APIs & Documentation
- **Spring WebFlux** or **Spring MVC** for REST
- **SpringDoc OpenAPI** (Swagger 3.0) for API documentation

### AI Integration
- **LangChain4j** for AI orchestration
- **OpenAI** / **Azure OpenAI** for LLM models

### Testing
- **JUnit 5** and **Mockito** for unit tests
- **Spring Boot Test** for integration tests
- **Embedded MongoDB** for test database

### Code Generation
- **MapStruct** for DTO mapping
- **Lombok** for reducing boilerplate

## Configuration

### Environment Variables

```bash
# MongoDB
MONGODB_USER=<user>
MONGODB_PASSWORD=<password>
MONGODB_CLUSTER=<cluster>
MONGODB_DATABASE=ai_legacy_modernization

# JWT
JWT_SECRET=<256-bit-secret-key>

# OpenAI
OPENAI_API_KEY=<api-key>
LLM_MODEL=gpt-4
LLM_TEMPERATURE=0.7
LLM_MAX_TOKENS=4096

# Application
APP_ADMIN_PASSWORD=<password>
TEMP_UPLOAD_DIR=/tmp/ai-legacy-copilot
OUTPUT_DIR=/var/ai-legacy-copilot/output

# Azure OpenAI (optional)
AZURE_OPENAI_API_KEY=<api-key>
AZURE_OPENAI_ENDPOINT=<endpoint>
AZURE_LLM_MODEL=gpt-4
AZURE_LLM_DEPLOYMENT_ID=<deployment-id>
```

### Profiles

- **dev**: Local development with embedded MongoDB, debug logging
- **prod**: Production with external MongoDB, optimal logging

## Exception Handling

All exceptions are caught by `GlobalExceptionHandler` and converted to standardized API responses:

```json
{
  "success": false,
  "message": "Resource not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2024-07-11T10:30:00"
}
```

## Response Format

All successful responses follow this format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* response payload */ },
  "timestamp": "2024-07-11T10:30:00"
}
```

## Build & Run

```bash
# Build
mvn clean install

# Run in development
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Run in production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Run tests
mvn test

# Package for deployment
mvn package -DskipTests
```

## Next Steps

1. **Implement domain entities** in `domain.entities/`
2. **Create MongoDB documents** in `infrastructure.persistence/`
3. **Build repositories** extending Spring Data MongoRepository
4. **Implement application services** in `application.services/`
5. **Create REST controllers** with appropriate DTO validation
6. **Implement analysis engines** in `infrastructure.analysis/`
7. **Integrate LangChain4j agents** in `infrastructure.ai/`
8. **Write comprehensive tests** in `src/test/`
