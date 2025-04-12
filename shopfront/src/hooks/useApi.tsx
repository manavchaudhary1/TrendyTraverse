
import { useState, useCallback } from 'react';
import { useToast } from '@/hooks/use-toast';
import { useAuth } from '@/contexts/AuthContext';

interface UseApiState<T> {
  data: T | null;
  isLoading: boolean;
  error: string | null;
}

export function useApi<T>() {
  const [state, setState] = useState<UseApiState<T>>({
    data: null,
    isLoading: false,
    error: null,
  });

  const { refreshAccessToken } = useAuth();
  const { toast } = useToast();

  const execute = useCallback(
    async (apiCall: () => Promise<any>, successMessage?: string) => {
      try {
        setState((prev) => ({ ...prev, isLoading: true, error: null }));
        
        const response = await apiCall();
        
        setState({
          data: response.data,
          isLoading: false,
          error: null,
        });
        
        if (successMessage) {
          toast({
            title: "Success",
            description: successMessage,
          });
        }
        
        return response.data;
      } catch (err: any) {
        console.error('API Error:', err);
        
        // Handle 401 errors (unauthorized) by refreshing the token
        if (err.response && err.response.status === 401) {
          try {
            await refreshAccessToken();
            // Retry the original call
            return execute(apiCall, successMessage);
          } catch (refreshError) {
            // If refresh also fails, handle the error
            const errorMessage = 'Session expired. Please log in again.';
            setState({
              data: null,
              isLoading: false,
              error: errorMessage,
            });
            toast({
              variant: "destructive",
              title: "Authentication Error",
              description: errorMessage,
            });
            return null;
          }
        }
        
        // Handle other errors
        const errorMessage =
          err.response?.data?.message || 'An unexpected error occurred';
        
        setState({
          data: null,
          isLoading: false,
          error: errorMessage,
        });
        
        toast({
          variant: "destructive",
          title: "Error",
          description: errorMessage,
        });
        
        return null;
      }
    },
    [refreshAccessToken, toast]
  );

  const reset = useCallback(() => {
    setState({
      data: null,
      isLoading: false,
      error: null,
    });
  }, []);

  return {
    ...state,
    execute,
    reset,
  };
}
