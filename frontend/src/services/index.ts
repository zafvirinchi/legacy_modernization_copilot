/**
 * Service Layer - API Integration
 */

import { apiClient } from '@/lib/api-client';
import { API_ENDPOINTS } from '@/constants';
import {
  ApiResponse,
  AuthTokenResponse,
  Project,
  Scan,
  Issue,
  Recommendation,
  Report,
  HealthStatus,
  Role,
  User,
} from '@/types';
import { getRefreshToken } from '@/utils/auth';

/**
 * Health Service
 */
export const healthService = {
  check: async (): Promise<HealthStatus> => {
    const response = await apiClient.get<HealthStatus>(API_ENDPOINTS.HEALTH);
    return response.data;
  },
};

/**
 * Authentication Service
 */
export const authService = {
  login: async (email: string, password: string): Promise<AuthTokenResponse> => {
    const response = await apiClient.post<ApiResponse<AuthTokenResponse>>(API_ENDPOINTS.AUTH_LOGIN, {
      email,
      password,
    });
    return response.data.data;
  },
  register: async (name: string, email: string, password: string, role: Role): Promise<AuthTokenResponse> => {
    const response = await apiClient.post<ApiResponse<AuthTokenResponse>>(API_ENDPOINTS.AUTH_REGISTER, {
      name,
      email,
      password,
      role,
    });
    return response.data.data;
  },
  logout: async (): Promise<void> => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) return;
    await apiClient.post(API_ENDPOINTS.AUTH_LOGOUT, { refreshToken });
  },
  getMe: async (): Promise<User> => {
    const response = await apiClient.get<ApiResponse<User>>(API_ENDPOINTS.AUTH_ME);
    return response.data.data;
  },
};

/**
 * Project Service
 */
export const projectService = {
  list: async (): Promise<Project[]> => {
    const response = await apiClient.get<Project[]>(API_ENDPOINTS.PROJECTS_LIST);
    return response.data;
  },
  create: async (data: Partial<Project>): Promise<Project> => {
    const response = await apiClient.post<Project>(API_ENDPOINTS.PROJECTS_CREATE, data);
    return response.data;
  },
  get: async (id: string): Promise<Project> => {
    const response = await apiClient.get<Project>(API_ENDPOINTS.PROJECTS_GET(id));
    return response.data;
  },
  update: async (id: string, data: Partial<Project>): Promise<Project> => {
    const response = await apiClient.put<Project>(API_ENDPOINTS.PROJECTS_UPDATE(id), data);
    return response.data;
  },  delete: async (id: string) => {
    return apiClient.delete(API_ENDPOINTS.PROJECTS_DELETE(id));
  },
};

/**
 * Scan Service
 */
export const scanService = {
  list: async (projectId: string): Promise<Scan[]> => {
    const response = await apiClient.get<Scan[]>(API_ENDPOINTS.SCANS_LIST(projectId));
    return response.data;
  },
  create: async (projectId: string): Promise<Scan> => {
    const response = await apiClient.post<Scan>(API_ENDPOINTS.SCANS_CREATE(projectId), {});
    return response.data;
  },
  get: async (scanId: string): Promise<Scan> => {
    const response = await apiClient.get<Scan>(API_ENDPOINTS.SCANS_GET(scanId));
    return response.data;
  },
  getStatus: async (scanId: string) => {
    const response = await apiClient.get(API_ENDPOINTS.SCANS_STATUS(scanId));
    return response.data;
  },
  rerun: async (scanId: string): Promise<Scan> => {
    const response = await apiClient.post<Scan>(API_ENDPOINTS.SCANS_RERUN(scanId), {});
    return response.data;
  },
};

/**
 * Analysis Service
 */
export const analysisService = {
  getIssues: async (scanId: string): Promise<Issue[]> => {
    const response = await apiClient.get<Issue[]>(API_ENDPOINTS.ISSUES_LIST(scanId));
    return response.data;
  },
  getRecommendations: async (scanId: string): Promise<Recommendation[]> => {
    const response = await apiClient.get<Recommendation[]>(API_ENDPOINTS.RECOMMENDATIONS_LIST(scanId));
    return response.data;
  },
};

/**
 * Report Service
 */
export const reportService = {
  list: async (scanId: string): Promise<Report[]> => {
    const response = await apiClient.get<Report[]>(API_ENDPOINTS.REPORTS_LIST(scanId));
    return response.data;
  },
  create: async (scanId: string, type: string): Promise<Report> => {
    const response = await apiClient.post<Report>(API_ENDPOINTS.REPORTS_CREATE(scanId), { type });
    return response.data;
  },
  get: async (reportId: string): Promise<Report> => {
    const response = await apiClient.get<Report>(API_ENDPOINTS.REPORTS_GET(reportId));
    return response.data;
  },
  download: async (reportId: string) => {
    const response = await apiClient.get<Blob>(API_ENDPOINTS.REPORTS_DOWNLOAD(reportId), {
      responseType: 'blob',
    });
    return response.data;
  },
};
