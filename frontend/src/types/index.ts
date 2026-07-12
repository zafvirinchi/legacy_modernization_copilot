/**
 * Type Definitions for the Application
 */

export interface Project {
  id: string;
  name: string;
  originalFileName: string;
  totalFiles: number;
  totalSizeBytes: number;
  fileExtensionBreakdown: Record<string, number>;
  createdAt: string;
}

export interface Scan {
  id: string;
  projectId: string;
  status: 'pending' | 'running' | 'completed' | 'failed';
  startedAt: Date;
  completedAt?: Date;
  issueCount: number;
}

export interface Issue {
  id: string;
  scanId: string;
  category: 'architecture' | 'security' | 'scalability' | 'maintainability';
  severity: 'critical' | 'high' | 'medium' | 'low';
  title: string;
  description: string;
  location?: string;
}

export interface Recommendation {
  id: string;
  scanId: string;
  title: string;
  description: string;
  priority: 'critical' | 'high' | 'medium' | 'low';
  codeExample?: string;
}

export interface Report {
  id: string;
  scanId: string;
  type: 'executive' | 'technical';
  format: 'pdf' | 'html';
  generatedAt: Date;
  path: string;
}

export type TechnologyType =
  | 'SERVLET'
  | 'JSP'
  | 'SPRING_MVC'
  | 'SPRING_XML'
  | 'JDBC'
  | 'HIBERNATE'
  | 'EJB'
  | 'COBOL'
  | 'JCL'
  | 'STRUTS';

export interface DetectedTechnology {
  technology: TechnologyType;
  confidenceScore: number;
  evidence: string[];
}

export interface TechnologyDetectionResult {
  id: string;
  projectId: string;
  detectedTechnologies: DetectedTechnology[];
  javaVersion: string;
  databases: string[];
  buildTool: string;
  applicationServer: string;
  createdAt: string;
}

export type Role = 'ADMIN' | 'ARCHITECT' | 'DEVELOPER';

export interface User {
  id: string;
  email: string;
  name: string;
  role: Role;
  createdAt: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  errors?: string[];
  errorCode?: string;
  timestamp?: string;
}

export interface HealthStatus {
  status: string;
  service: string;
  version: string;
  environment: string;
  timestamp: string;
}
