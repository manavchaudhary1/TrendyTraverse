
import { publicApi } from './axiosConfig';
import { LoginCredentials, SignupData, TokenResponse } from '../entities/User';

// User service API endpoints (all public)
export const userApi = {
  signUp: (userData: SignupData) => 
    publicApi.post<{ message: string }>('/user-service/users/create', userData),
  
  login: (credentials: LoginCredentials) => 
    publicApi.post<TokenResponse>('/user-service/users/token', credentials),
  
  refreshToken: (refreshToken: string) => 
    publicApi.post<TokenResponse>('/user-service/users/refresh-token', { refreshToken }),
  
  promoteUser: (username: string) => 
    publicApi.post<{ message: string }>(`/user-service/users/promote/${username}`),
};
