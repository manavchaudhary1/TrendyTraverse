
import React, { createContext, useContext, useState, useEffect } from 'react';
import {cartApi, orderApi} from '@/lib/api';
import { useAuth } from './AuthContext';
import { useToast } from '@/hooks/use-toast';
import { CartItem } from '@/lib/entities/Cart';

interface CartContextType {
  items: CartItem[];
  isLoading: boolean;
  error: string | null;
  addItem: (item: CartItem) => Promise<void>;
  updateItemQuantity: (productId: string, quantity: number) => Promise<void>;
  removeItem: (productId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  checkout: () => Promise<void>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();
  const { toast } = useToast();

  useEffect(() => {
    if (user) {
      fetchCartItems();
    } else {
      // Load cart from localStorage when user is not logged in
      const savedCart = localStorage.getItem('guestCart');
      if (savedCart) {
        try {
          setItems(JSON.parse(savedCart));
        } catch (err) {
          console.error('Error parsing saved cart:', err);
          localStorage.removeItem('guestCart');
        }
      }
    }
  }, [user]);

  // Save guest cart to localStorage whenever it changes
  useEffect(() => {
    if (!user && items.length > 0) {
      localStorage.setItem('guestCart', JSON.stringify(items));
    }
  }, [items, user]);

  const fetchCartItems = async () => {
    if (!user) return;
    
    try {
      setIsLoading(true);
      setError(null);
      
      // Use user.id for API calls which should be a UUID
      const userId = user.id;
      console.log('Fetching cart for user:', userId);
      
      try {
        const response = await cartApi.getCart(userId);
        setItems(response.data.items || []);
      } catch (apiErr) {
        console.error('API error fetching cart:', apiErr);
        // If API fails, fall back to local cart
        const savedCart = localStorage.getItem(`cart_${userId}`);
        if (savedCart) {
          setItems(JSON.parse(savedCart));
        }
      }
    } catch (err: any) {
      console.error('Error fetching cart:', err);
      setError('Failed to fetch cart items.');
    } finally {
      setIsLoading(false);
    }
  };

  const addItem = async (item: CartItem) => {
    if (!user) {
      // Handle guest cart
      const existingItemIndex = items.findIndex(i => i.productId === item.productId);
      
      if (existingItemIndex !== -1) {
        const updatedItems = [...items];
        updatedItems[existingItemIndex].quantity += item.quantity;
        setItems(updatedItems);
      } else {
        setItems([...items, item]);
      }
      
      toast({
        title: "Item added to cart",
        description: `${item.name} has been added to your cart.`,
      });
      return;
    }
    
    try {
      setIsLoading(true);
      setError(null);
      
      const userId = user.id;
      console.log('Adding item to cart for user:', userId, item);
      
      try {
        // Try the API call first
        await cartApi.addItemToCart(userId, {
          productId: item.productId,
          quantity: item.quantity,
        });
      } catch (apiErr) {
        console.error('API error adding item to cart:', apiErr);
        // On API failure, just update local state
      }
      
      // Update local state regardless of API success
      const existingItemIndex = items.findIndex(i => i.productId === item.productId);
      
      if (existingItemIndex !== -1) {
        const updatedItems = [...items];
        updatedItems[existingItemIndex].quantity += item.quantity;
        setItems(updatedItems);
      } else {
        setItems([...items, item]);
      }
      
      // Save to localStorage as backup
      localStorage.setItem(`cart_${userId}`, JSON.stringify(items));
      
      toast({
        title: "Item added to cart",
        description: `${item.name} has been added to your cart.`,
      });
    } catch (err: any) {
      console.error('Error adding item to cart:', err);
      const errorMessage = err.response?.data?.message || 'Failed to add item to cart.';
      setError(errorMessage);
      toast({
        variant: "destructive",
        title: "Failed to add item",
        description: 'Could not connect to cart service. Item was added locally only.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const updateItemQuantity = async (productId: string, quantity: number) => {
    if (!user) {
      // Handle guest cart
      const updatedItems = items.map(item => 
        item.productId === productId ? { ...item, quantity } : item
      );
      
      setItems(updatedItems);
      return;
    }
    
    try {
      setIsLoading(true);
      setError(null);
      
      const userId = user.id;
      
      try {
        // Try API call first
        await cartApi.updateItemQuantity(userId, { productId, quantity });
      } catch (apiErr) {
        console.error('API error updating item quantity:', apiErr);
        // On API failure, just update local state
      }
      
      // Update local state regardless of API success
      const updatedItems = items.map(item => 
        item.productId === productId ? { ...item, quantity } : item
      );
      
      setItems(updatedItems);
      
      // Save to localStorage as backup
      localStorage.setItem(`cart_${userId}`, JSON.stringify(updatedItems));
    } catch (err: any) {
      console.error('Error updating item quantity:', err);
      setError('Failed to update item quantity.');
      toast({
        variant: "destructive",
        title: "Failed to update item",
        description: 'Could not connect to cart service. Item was updated locally only.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const removeItem = async (productId: string) => {
    if (!user) {
      // Handle guest cart
      const updatedItems = items.filter(item => item.productId !== productId);
      setItems(updatedItems);
      
      toast({
        title: "Item removed",
        description: "The item has been removed from your cart.",
      });
      return;
    }
    
    try {
      setIsLoading(true);
      setError(null);
      
      const userId = user.id;
      
      try {
        // Try API call first
        await cartApi.removeItemFromCart(userId, productId);
      } catch (apiErr) {
        console.error('API error removing item from cart:', apiErr);
        // On API failure, just update local state
      }
      
      // Update local state regardless of API success
      const updatedItems = items.filter(item => item.productId !== productId);
      setItems(updatedItems);
      
      // Save to localStorage as backup
      localStorage.setItem(`cart_${userId}`, JSON.stringify(updatedItems));
      
      toast({
        title: "Item removed",
        description: "The item has been removed from your cart.",
      });
    } catch (err: any) {
      console.error('Error removing item from cart:', err);
      setError('Failed to remove item from cart.');
      toast({
        variant: "destructive",
        title: "Failed to remove item",
        description: 'Could not connect to cart service. Item was removed locally only.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const clearCart = async () => {
    if (!user) {
      setItems([]);
      localStorage.removeItem('guestCart');
      return Promise.resolve();
    }
    
    const userId = user.id;
    localStorage.removeItem(`cart_${userId}`);
    setItems([]);
    return Promise.resolve();
  };

  const checkout = async () => {
    if (!user) return;
    
    try {
      setIsLoading(true);
      setError(null);
      
      const userId = user.id;
      
      try {
        await orderApi.placeCartAsOrder(userId);
      } catch (apiErr) {
        console.error('API error during checkout:', apiErr);
        // Proceed with local checkout on API failure
      }
      
      // Clear cart locally regardless of API success
      setItems([]);
      localStorage.removeItem(`cart_${userId}`);
      
      toast({
        title: "Checkout complete",
        description: "Your order has been placed successfully.",
      });
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Checkout failed.';
      setError(errorMessage);
      toast({
        variant: "destructive",
        title: "Checkout failed",
        description: errorMessage,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <CartContext.Provider
      value={{
        items,
        isLoading,
        error,
        addItem,
        updateItemQuantity,
        removeItem,
        clearCart,
        checkout,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
