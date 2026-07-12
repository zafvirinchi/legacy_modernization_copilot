import Cookies from 'js-cookie';

const ACCESS_TOKEN_KEY = 'auth_access_token';
const REFRESH_TOKEN_KEY = 'auth_refresh_token';

export const getAuthToken = (): string | undefined => {
  return Cookies.get(ACCESS_TOKEN_KEY);
};

export const setAuthToken = (token: string): void => {
  Cookies.set(ACCESS_TOKEN_KEY, token, { expires: 1, sameSite: 'strict' });
};

export const getRefreshToken = (): string | undefined => {
  return Cookies.get(REFRESH_TOKEN_KEY);
};

export const setRefreshToken = (token: string): void => {
  Cookies.set(REFRESH_TOKEN_KEY, token, { expires: 7, sameSite: 'strict' });
};

export const setAuthTokens = (accessToken: string, refreshToken: string): void => {
  setAuthToken(accessToken);
  setRefreshToken(refreshToken);
};

export const removeAuthToken = (): void => {
  Cookies.remove(ACCESS_TOKEN_KEY);
  Cookies.remove(REFRESH_TOKEN_KEY);
};
