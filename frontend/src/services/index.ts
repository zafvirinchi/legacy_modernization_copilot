/**
 * Service Layer - API Integration
 */

import { apiClient } from '@/lib/api-client';
import { API_ENDPOINTS } from '@/constants';
import { Project, Scan, Issue, Recommendation, Report, AuthResponse } from '@/types';

/**
 * Authentication Service
 */
export const authService = {
  login: async (email: string, password: string): Promise<AuthResponse> => {
    const response = await apiClient.post(API_ENDPOINTS.AUTH_LOGIN, { email, password });
    return response.data;
  },
  register: async (email: string, password: string, name: string): Promise<AuthResponse> => {
    const response = await apiClient.post(API_ENDPOINTS.AUTH_REGISTER, { email, password, name });
    return response.data;
  },
  logout: async () => {
    return apiClient.post(API_ENDPOINTS.AUTH_LOGOUT);
  },
  getMe: async () => {
    const response = await apiClient.get(API_ENDPOINTS.AUTH_ME);
    return response.data;
  },
};

/**
 * Project Service
 */
export const projectService = {
  list: async (): Promise<Project[]> => {
    const response = await apiClient.get(API_ENDPOINTS.PROJECTS_LIST);
    return response.data;
  },
  create: async (data: Partial<Project>): Promise<Project> => {
    const response = await apiClient.post(API_ENDPOINTS.PROJECTS_CREATE, data);
    return response.data;
  },
  get: async (id: string): Promise<Project> => {
    const response = await apiClient.get(API_ENDPOINTS.PROJECTS_GET(id));
    return response.data;
  },
  update: async (id: string, data: Partial<Project>): Promise<Project> => {
    const response = await apiClient.put(API_ENDPOINTS.PROJECTS_UPDATE(id), data);
    return response.data;
  },
  delete: async (id: string) => {
    return apiClient.delete(API_ENDPOINTS.PROJECTS_DELETE(id));
  },
};

/**
 * Scan Service
 */
export const scanService = {
  list: async (projectId: string): Promise<Scan[]> => {
    const response = await apiClient.get(API_ENDPOINTS.SCANS_LIST(projectId));
    return response.data;
  },
  create: async (projectId: string): Promise<Scan> => {
    const response = await apiClient.post(API_ENDPOINTS.SCANS_CREATE(projectId), {});
    return response.data;
  },
  get: async (scanId: string): Promise<Scan> => {
    const response = await apiClient.get(API_ENDPOINTS.SCANS_GET(scanId));
    return response.data;
  },
  getStatus: async (scanId: string) => {
    const response = await apiClient.get(API_ENDPOINTS.SCANS_STATUS(scanId));
    return response.data;
  },
  rerun: async (scanId: string): Promise<Scan> => {
    const response = await apiClient.post(API_ENDPOINTS.SCANS_RERUN(scanId), {});
    return response.data;
  },
};

/**
 * Analysis Service
 */
export const analysisService = {
  getIssues: async (scanId: string): Promise<Issue[]> => {
    const response = await apiClient.get(API_ENDPOINTS.ISSUES_LIST(scanId));
    return response.data;
  },
  getRecommendations: async (scanId: string): Promise<Recommendation[]> => {
    const response = await apiClient.get(API_ENDPOINTS.RECOMMENDATIONS_LIST(scanId));
    return response.data;
  },
};

/**
 * Report Service
 */
export const reportService = {
  list: async (scanId: string): Promise<Report[]> => {
    const response = await apiClient.get(API_ENDPOINTS.REPORTS_LIST(scanId));
    return response.data;
  },
  create: async (scanId: string, type: string): Promise<Report> => {
    const response = await apiClient.post(API_ENDPOINTS.REPORTS_CREATE(scanId), { type });
    return response.data;
  },
  get: async (reportId: string): Promise<Report> => {
    const response = await apiClient.get(API_ENDPOINTS.REPORTS_GET(reportId));
    return response.data;
  },
  download: async (reportId: string) => {
    const response = await apiClient.get(API_ENDPOINTS.REPORTS_DOWNLOAD(reportId), {
      responseType: 'blob',
    });
    return response.data;
  },
};
