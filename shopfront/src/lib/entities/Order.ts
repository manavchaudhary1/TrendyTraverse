
export interface OrderData {
  productId: number;
  quantity: number;
}

export interface OrderLine {
  productId: number;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  userId: string;
  orderLines: OrderLine[];
  createdAt: string;
  status?: string;
  totalAmount?: number;
  total?: number; // Added for backward compatibility with OrdersPage
  orderDate?: string; // Added for backward compatibility with OrdersPage
  items?: OrderItem[]; // Added for backward compatibility with OrdersPage
}

export interface OrderItem {
  productId: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}
