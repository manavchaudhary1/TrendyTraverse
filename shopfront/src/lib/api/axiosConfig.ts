import axios from 'axios';

// Determine the base URL based on environment
const determineBaseUrl = () => {
    // When running locally on the host machine
    if (typeof window !== 'undefined' && window.location.hostname === 'localhost') {
        return 'http://localhost:8072';
    }
    // When running inside Docker
    return 'http://gateway-server:8072';
};

// Base URL for API requests
const BASE_URL = determineBaseUrl();

// Create axios instance for non-authenticated requests
export const publicApi = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Enable sending credentials (cookies, etc.)
});

// Create axios instance for authenticated requests
export const privateApi = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Enable sending credentials (cookies, etc.)
});

// Track if a token refresh is in progress
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

// Helper function to subscribe failed requests to be retried after token refresh
const subscribeTokenRefresh = (callback: (token: string) => void) => {
    refreshSubscribers.push(callback);
};

// Helper function to retry failed requests after token refresh
const onTokenRefreshed = (newToken: string) => {
    refreshSubscribers.forEach(callback => callback(newToken));
    refreshSubscribers = [];
};

// Request interceptor to add auth token only for authenticated requests
privateApi.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor to handle token refresh on 401 errors
privateApi.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // If the error is not 401 or the request has already been retried, reject
        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        // If token refresh is in progress, queue this request
        if (isRefreshing) {
            return new Promise(resolve => {
                subscribeTokenRefresh(token => {
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    resolve(axios(originalRequest));
                });
            });
        }

        // Mark that we're refreshing and this request is being retried
        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const refreshToken = localStorage.getItem('refreshToken');
            if (!refreshToken) {
                throw new Error('No refresh token available');
            }

            // Use a separate axios instance to avoid interceptors loop
            const response = await axios.post(`${BASE_URL}/user-service/users/refresh-token`,
                { refreshToken },
                { headers: { 'Content-Type': 'application/json' } }
            );

            const { access_token: newAccessToken,refresh_token: newRefreshToken  } = response.data;

            // Update tokens in localStorage
            localStorage.setItem('accessToken', newAccessToken);
            if (newRefreshToken) {
                localStorage.setItem('refreshToken', newRefreshToken);
            }
            // Notify subscribers that token has been refreshed
            onTokenRefreshed(newAccessToken);

            // Update auth header and retry the original request
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return axios(originalRequest);
        } catch (refreshError) {
            // Clear auth data if refresh fails
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('userId');
            localStorage.removeItem('user');

            // Redirect to login page (handled by consumer)
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

// Setup token refresh timer
export const setupTokenRefreshTimer = () => {
    // Clear any existing timers
    if (window.tokenRefreshTimer) {
        clearInterval(window.tokenRefreshTimer);
    }

    // Set up a new timer that refreshes the token every 5 minutes
    window.tokenRefreshTimer = setInterval(async () => {
        const refreshToken = localStorage.getItem('refreshToken');
        const accessToken = localStorage.getItem('accessToken');

        // Only refresh if we have both tokens
        if (refreshToken && accessToken) {
            try {
                const response = await axios.post(`${BASE_URL}/user-service/users/refresh-token`,
                    { refreshToken },
                    { headers: { 'Content-Type': 'application/json' } }
                );

                const { access_token: newAccessToken, refresh_token: newRefreshToken } = response.data;
                localStorage.setItem('accessToken', newAccessToken);
                if (newRefreshToken) {
                    localStorage.setItem('refreshToken', newRefreshToken);
                }
                console.log('Access token refreshed automatically');
            } catch (error) {
                // If refresh fails, we'll let the interceptor handle it when a request fails
                console.error('Failed to refresh token:', error);
            }
        }
    }, 5 * 60 * 1000); // 5 minutes

    // Clean up timer on page unload
    window.addEventListener('beforeunload', () => {
        if (window.tokenRefreshTimer) {
            clearInterval(window.tokenRefreshTimer);
        }
    });
};
