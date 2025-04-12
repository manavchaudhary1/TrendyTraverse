
import { ReviewDTO } from './Review';

export interface Product {
  id: string;
  name: string;
  price: number;
  imageUrls: string[];
  brand: string;
  availabilityStatus: string;
}

export interface ProductDTO {
  productId: number;
  name: string;
  brand: string | null;
  productImages: ProductImage[];
  fullDescription: string | null;
  featureBullets: string[];
  pricing: number;
  listPrice: number;
  availabilityStatus: string | null;
  productCategory: string;
  productDimensions: string | null;
  dateFirstAvailable: string;
  manufacturer: string | null;
  countryOfOrigin: string | null;
  averageRating: number;
  totalReviews: number;
  fiveStarReviews: number;
  fourStarReviews: number;
  threeStarReviews: number;
  twoStarReviews: number;
  oneStarReviews: number;
  reviews: ReviewDTO[];
  
  // Virtual properties to help with compatibility
  id?: string;
  price?: number;
  imageUrls?: string[];
}

interface ProductImage {
  imageId: number;
  imageUrl: string;
}

export interface ProductSearchResultDTO {
  productId: number;
  name: string;
  pricing: number;
  firstImage?: string;
  brand?: string;
}

