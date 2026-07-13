/**
 * HTTP Client using Axios
 * Centralized API communication with interceptors
 */

import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
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
      // No default Content-Type here: axios already auto-detects and JSON-encodes
      // plain object bodies on its own. A default 'application/json' would also
      // apply to FormData upload bodies, and axios treats "FormData + declared
      // JSON content-type" as a signal to coerce the FormData into a JSON object
      // (converting each File entry to '{}'), breaking uploads.
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor
    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const token = getAuthToken();
        if (token) {
          // Mutate the existing AxiosHeaders instance rather than spreading it into a
          // plain object - spreading strips its class methods, which axios relies on
          // internally (e.g. to detect FormData bodies and let the browser set the
          // multipart boundary instead of JSON-stringifying the body).
          config.headers.set('Authorization', `Bearer ${token}`);
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
          originalRequest.headers.set('Authorization', `Bearer ${newAccessToken}`);
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
    // Let the browser set Content-Type for FormData bodies - it must include a
    // boundary parameter that only the browser can generate. Setting it manually
    // here would produce a boundary-less header the server can't parse as multipart.
    return this.client.post<T>(url, formData, config);
  }
}

export const apiClient = new ApiClient();
