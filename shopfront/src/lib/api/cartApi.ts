
import { privateApi } from './axiosConfig';
import { CartRequestItem, CartResponse } from '../entities/Cart';

// Cart service API endpoints (all need token)
export const cartApi = {
  getCart: (userId: string) => 
    privateApi.get<CartResponse>(`/cart-service/cart/${userId}`),
  
  addItemToCart: (userId: string, item: CartRequestItem) => 
    privateApi.post(`/cart-service/cart/${userId}/items`, item),
  
  updateItemQuantity: (userId: string, item: CartRequestItem) => 
    privateApi.put(`/cart-service/cart/${userId}/items`, item),
  
  removeItemFromCart: (userId: string, productId: string) => 
    privateApi.delete(`/cart-service/cart/${userId}/items/${productId}`),
  
  checkout: (userId: string) => 
    privateApi.post(`/cart-service/cart/${userId}/checkout`),
};
