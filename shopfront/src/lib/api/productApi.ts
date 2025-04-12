
import { publicApi, privateApi } from './axiosConfig';
import { Product, ProductDTO, ProductSearchResultDTO } from '../entities/Product';

// Product service API endpoints
export const productApi = {
  // Public endpoints (no token needed)
  getProduct: (productId: string) => 
    publicApi.get<ProductDTO>(`/product-service/products/${productId}`),
  
  getProducts: (keyword = '') => 
    publicApi.get<ProductSearchResultDTO[]>(`/product-service/products/search?keyword=${encodeURIComponent(keyword)}`).then(response => {
      // Map the ProductSearchResultDTO to the expected Product format
      return {
        ...response,
        data: response.data.map((item: ProductSearchResultDTO) => ({
          id: item.productId.toString(),
          name: item.name,
          price: item.pricing, // Keep the original price format without dividing
          imageUrls: item.firstImage ? [item.firstImage] : [],
          brand: item.brand || 'Unknown', // Default value if not provided
          availabilityStatus: 'In Stock' // Default value
        }))
      };
    }),
  
  // Protected endpoints (token required)
  createProduct: (productData: Partial<ProductDTO>) => 
    privateApi.post<ProductDTO>('/product-service/products', productData),
  
  updateProduct: (productId: string, productData: Partial<ProductDTO>) => 
    privateApi.put<ProductDTO>(`/product-service/products/${productId}`, productData),
  
  deleteProduct: (productId: string) => 
    privateApi.delete(`/product-service/products/${productId}`),
};
