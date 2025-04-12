
export interface User {
  id: string;
  username: string;
  email: string;
  isAdmin: boolean;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface SignupData {
  username: string;
  email: string;
  password: string;
}

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  refresh_expires_in: number;
  expires_in: number;
  userId: string;
}
