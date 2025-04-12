import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '@/contexts/CartContext';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { TrashIcon, MinusIcon, PlusIcon, ShoppingBag, ArrowRight } from 'lucide-react';
import { productApi } from '@/lib/api';
import { toast } from 'sonner';

interface EnhancedCartItem {
  productId: string;
  name?: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}

const CartPage = () => {
  const { items, updateItemQuantity, removeItem, checkout, isLoading } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [enhancedItems, setEnhancedItems] = useState<EnhancedCartItem[]>([]);
  const [isEnhancing, setIsEnhancing] = useState(false);

  useEffect(() => {
    const enhanceCartItems = async () => {
      const needsEnhancement = items.some(item => !item.name || !item.imageUrl);
      
      if (!needsEnhancement || items.length === 0) {
        setEnhancedItems(items);
        return;
      }

      setIsEnhancing(true);
      
      try {
        const enhanced = await Promise.all(
          items.map(async (item) => {
            if (item.name && item.imageUrl) {
              return item;
            }
            
            try {
              const response = await productApi.getProduct(item.productId);
              const product = response.data;
              
              return {
                ...item,
                name: product.name || `Product ${item.productId}`,
                imageUrl: product.productImages?.[0]?.imageUrl || undefined
              };
            } catch (error) {
              console.error(`Failed to fetch details for product ${item.productId}:`, error);
              return {
                ...item,
                name: item.name || `Product ${item.productId}`,
                imageUrl: item.imageUrl || undefined
              };
            }
          })
        );
        
        setEnhancedItems(enhanced);
      } catch (error) {
        console.error('Error enhancing cart items:', error);
        toast.error('Could not load some product details');
        setEnhancedItems(items);
      } finally {
        setIsEnhancing(false);
      }
    };

    enhanceCartItems();
  }, [items]);

  const subtotal = enhancedItems.reduce(
    (sum, item) => sum + item.price * item.quantity, 
    0
  );
  const shipping = subtotal > 50 ? 0 : 5.99;
  const total = subtotal + shipping;

  const handleCheckout = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    
    try {
      await checkout();
      navigate('/checkout-success');
    } catch (error) {
      console.error('Checkout failed:', error);
    }
  };

  const isPageLoading = isLoading || isEnhancing;

  if (enhancedItems.length === 0 && !isPageLoading) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Card className="mx-auto max-w-lg">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl">Your Cart is Empty</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col items-center py-8">
            <ShoppingBag className="h-16 w-16 text-gray-300 mb-4" />
            <p className="text-gray-500 mb-6">
              Looks like you haven't added any items to your cart yet.
            </p>
            <Link to="/products">
              <Button>
                Start Shopping
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Your Shopping Cart</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="md:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Items ({enhancedItems.length})</CardTitle>
            </CardHeader>
            <CardContent>
              {isPageLoading ? (
                <div className="flex justify-center py-8">
                  <div className="animate-pulse flex flex-col items-center">
                    <div className="h-10 w-10 bg-gray-200 rounded-full mb-4"></div>
                    <div className="h-4 w-48 bg-gray-200 rounded mb-2"></div>
                    <div className="h-4 w-32 bg-gray-200 rounded"></div>
                  </div>
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Product</TableHead>
                      <TableHead>Price</TableHead>
                      <TableHead>Quantity</TableHead>
                      <TableHead>Total</TableHead>
                      <TableHead className="w-[50px]"></TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {enhancedItems.map((item) => (
                      <TableRow key={item.productId}>
                        <TableCell>
                          <div className="flex items-center space-x-3">
                            <div className="flex-shrink-0 h-12 w-12 rounded overflow-hidden">
                              {item.imageUrl ? (
                                <img
                                  src={item.imageUrl}
                                  alt={item.name || `Product ${item.productId}`}
                                  className="h-full w-full object-cover"
                                />
                              ) : (
                                <div className="h-full w-full bg-gray-200 flex items-center justify-center text-gray-500 text-xs">
                                  No Image
                                </div>
                              )}
                            </div>
                            <div>
                              <Link 
                                to={`/products/${item.productId}`}
                                className="font-medium hover:text-purple-600 transition-colors"
                              >
                                {item.name || `Product ${item.productId}`}
                              </Link>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell>${item.price.toFixed(2)}</TableCell>
                        <TableCell>
                          <div className="flex items-center space-x-1">
                            <Button
                              variant="outline"
                              size="icon"
                              className="h-8 w-8"
                              onClick={() => updateItemQuantity(item.productId, Math.max(1, item.quantity - 1))}
                              disabled={isPageLoading || item.quantity <= 1}
                            >
                              <MinusIcon className="h-3 w-3" />
                            </Button>
                            <span className="w-8 text-center">{item.quantity}</span>
                            <Button
                              variant="outline"
                              size="icon"
                              className="h-8 w-8"
                              onClick={() => updateItemQuantity(item.productId, item.quantity + 1)}
                              disabled={isPageLoading}
                            >
                              <PlusIcon className="h-3 w-3" />
                            </Button>
                          </div>
                        </TableCell>
                        <TableCell className="font-medium">
                          ${(item.price * item.quantity).toFixed(2)}
                        </TableCell>
                        <TableCell>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 text-red-500 hover:text-red-600 hover:bg-red-50"
                            onClick={() => removeItem(item.productId)}
                            disabled={isPageLoading}
                          >
                            <TrashIcon className="h-4 w-4" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </CardContent>
            <CardFooter className="flex justify-between border-t pt-4">
              <Link to="/products">
                <Button variant="outline">
                  Continue Shopping
                </Button>
              </Link>
            </CardFooter>
          </Card>
        </div>

        <div>
          <Card>
            <CardHeader>
              <CardTitle>Order Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between">
                <span className="text-gray-600">Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Shipping</span>
                <span>
                  {shipping === 0 ? 'Free' : `$${shipping.toFixed(2)}`}
                </span>
              </div>
              {shipping > 0 && (
                <div className="text-sm text-gray-500">
                  Free shipping on orders over $50
                </div>
              )}
              <div className="pt-4 border-t">
                <div className="flex justify-between font-bold text-lg">
                  <span>Total</span>
                  <span>${total.toFixed(2)}</span>
                </div>
              </div>
            </CardContent>
            <CardFooter>
              <Button
                className="w-full"
                size="lg"
                onClick={handleCheckout}
                disabled={isPageLoading}
              >
                {isPageLoading ? 'Processing...' : 'Proceed to Checkout'}
              </Button>
            </CardFooter>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
