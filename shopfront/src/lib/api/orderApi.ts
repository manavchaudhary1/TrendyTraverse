
import { privateApi } from './axiosConfig';
import { Order, OrderData } from '../entities/Order';

// Order service API endpoints (all need token)
export const orderApi = {
  placeCartAsOrder: (userId: string) => 
    privateApi.post<Order>(`/order-service/orders/${userId}/cart`),
  
  getOrders: (userId: string) => 
    privateApi.get<Order[]>(`/order-service/orders/${userId}`),
  
  placeOrder: (userId: string, orderData: OrderData) => 
    privateApi.post<Order>(`/order-service/orders/${userId}`, orderData),
  
  deleteOrder: (userId: string, orderId: string) => 
    privateApi.delete(`/order-service/orders/${userId}/${orderId}`),
};
