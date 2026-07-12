/**
 * Type Definitions for the Application
 */

export interface Project {
  id: string;
  name: string;
  description: string;
  createdAt: Date;
  updatedAt: Date;
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

export interface User {
  id: string;
  email: string;
  name: string;
  role: 'admin' | 'user';
  createdAt: Date;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface HealthStatus {
  status: string;
  service: string;
  version: string;
  environment: string;
  timestamp: string;
}
