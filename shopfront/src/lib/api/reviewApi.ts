
import { publicApi, privateApi } from './axiosConfig';
import { ReviewDTO, ReviewData } from '../entities/Review';

// Review service API endpoints
export const reviewApi = {
  // Public endpoints (no token needed)
  getReviews: (productId: string) => 
    publicApi.get<ReviewDTO[]>(`/review-service/products/${productId}/reviews`),
  
  // Protected endpoints (token required)
  createReview: (productId: string, reviewData: ReviewData) => 
    privateApi.post<ReviewDTO>(`/review-service/products/${productId}/reviews`, reviewData),
};
