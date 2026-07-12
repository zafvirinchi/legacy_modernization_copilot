/**
 * HTTP Client using Axios
 * Centralized API communication with interceptors
 */

import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosRequestHeaders,
  InternalAxiosRequestConfig,
} from 'axios';
import { API_BASE_URL, API_ENDPOINTS } from '@/constants';
import { getAuthToken, getRefreshToken, removeAuthToken, setAuthTokens } from '@/utils/auth';
import type { ApiResponse } from '@/types';

interface RetriableRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

interface RefreshedTokens {
  accessToken: string;
  refreshToken: string;
}

class ApiClient {
  private client: AxiosInstance;
  private refreshPromise: Promise<string> | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor
    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const token = getAuthToken();
        if (token) {
          const headers = config.headers ?? {};
          config.headers = {
            ...headers,
            Authorization: `Bearer ${token}`,
          } as AxiosRequestHeaders;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor: on 401, attempt a single token refresh then retry once
    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        const originalRequest = error.config as RetriableRequestConfig | undefined;
        const isRefreshCall = originalRequest?.url === API_ENDPOINTS.AUTH_REFRESH;

        if (error.response?.status !== 401 || !originalRequest || originalRequest._retry || isRefreshCall) {
          if (error.response?.status === 401) {
            this.handleSessionExpired();
          }
          return Promise.reject(error);
        }

        originalRequest._retry = true;

        try {
          const newAccessToken = await this.refreshAccessToken();
          originalRequest.headers = {
            ...originalRequest.headers,
            Authorization: `Bearer ${newAccessToken}`,
          } as AxiosRequestHeaders;
          return this.client(originalRequest);
        } catch (refreshError) {
          this.handleSessionExpired();
          return Promise.reject(refreshError);
        }
      }
    );
  }

  private async refreshAccessToken(): Promise<string> {
    if (!this.refreshPromise) {
      this.refreshPromise = this.performRefresh().finally(() => {
        this.refreshPromise = null;
      });
    }
    return this.refreshPromise;
  }

  private async performRefresh(): Promise<string> {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await this.client.post<ApiResponse<RefreshedTokens>>(API_ENDPOINTS.AUTH_REFRESH, {
      refreshToken,
    });
    const tokens = response.data.data;
    setAuthTokens(tokens.accessToken, tokens.refreshToken);
    return tokens.accessToken;
  }

  private handleSessionExpired(): void {
    removeAuthToken();
    if (typeof window !== 'undefined') {
      window.location.href = '/login';
    }
  }

  get<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return this.client.get<T>(url, config);
  }

  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return this.client.post<T>(url, data, config);
  }

  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return this.client.put<T>(url, data, config);
  }

  delete<T = unknown>(url: string, config?: AxiosRequestConfig) {
    return this.client.delete<T>(url, config);
  }

  patch<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
    return this.client.patch<T>(url, data, config);
  }

  upload<T = unknown>(url: string, formData: FormData, config?: AxiosRequestConfig) {
    return this.client.post<T>(url, formData, {
      ...config,
      headers: {
        ...config?.headers,
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}

export const apiClient = new ApiClient();
