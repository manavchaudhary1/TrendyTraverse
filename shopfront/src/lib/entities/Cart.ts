
export interface CartItem {
  productId: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}

export interface CartRequestItem {
  productId: string;
  quantity: number;
}

export interface CartResponse {
  items: CartItem[];
}
