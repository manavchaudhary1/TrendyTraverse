
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { orderApi, productApi } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import { Order as OrderEntity, OrderLine } from '@/lib/entities/Order';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { ShoppingBag, ArrowRight, ExternalLink } from 'lucide-react';
import { useApi } from '@/hooks/useApi';
import { toast } from 'sonner';
import { Product } from '@/lib/entities/Product';

// Local interface for the component that matches the expected shape
interface OrderItem {
  productId: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}

interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  total: number;
  status: string;
  createdAt: string;
}

const OrdersPage = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchOrders = async () => {
      if (!user) return;

      try {
        setIsLoading(true);
        setError(null);
        const response = await orderApi.getOrders(user.id);
        
        // Process orders and fetch product details for each order line
        const ordersWithProducts = await Promise.all(
          response.data.map(async (order: OrderEntity) => {
            // Calculate total from orderLines
            const calculatedTotal = order.orderLines?.reduce(
              (total, line) => total + (line.price * line.quantity), 
              0
            ) || 0;

            // Fetch product details for each order line
            const itemsWithDetails = await Promise.all(
              (order.orderLines || []).map(async (line: OrderLine) => {
                try {
                  // Fetch product details
                  const productResponse = await productApi.getProduct(line.productId.toString());
                  const product = productResponse.data;
                  
                  return {
                    productId: line.productId.toString(),
                    name: product.name || `Product ID: ${line.productId}`,
                    price: line.price,
                    quantity: line.quantity,
                    imageUrl: product.productImages && product.productImages.length > 0 
                      ? product.productImages[0].imageUrl 
                      : undefined
                  };
                } catch (err) {
                  console.error(`Error fetching product ${line.productId}:`, err);
                  // Return basic info if product fetch fails
                  return {
                    productId: line.productId.toString(),
                    name: `Product ID: ${line.productId}`,
                    price: line.price,
                    quantity: line.quantity,
                    imageUrl: undefined
                  };
                }
              })
            );

            return {
              id: order.id,
              userId: order.userId,
              items: itemsWithDetails,
              total: calculatedTotal,
              status: order.status || 'Processing', // Default status if not provided
              createdAt: order.createdAt
            };
          })
        );
        
        setOrders(ordersWithProducts);
        console.log('Orders with products:', ordersWithProducts);
      } catch (err) {
        console.error('Error fetching orders:', err);
        setError('Failed to load your orders. Please try again.');
        // Use placeholder data for demo
        setOrders(placeholderOrders);
        toast.error('Could not load orders. Showing sample data instead.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchOrders();
  }, [user]);

  // Format date to readable string
  const formatDate = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // Placeholder orders for demo
  const placeholderOrders: Order[] = [
    {
      id: 'ord-001',
      userId: 'user123',
      items: [
        {
          productId: '1',
          name: 'Premium Wireless Headphones',
          price: 129.99,
          quantity: 1,
          imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e',
        },
        {
          productId: '6',
          name: 'Fitness Smartwatch',
          price: 149.99,
          quantity: 1,
          imageUrl: 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1',
        },
      ],
      total: 279.98,
      status: 'Delivered',
      createdAt: '2023-04-15',
    },
    {
      id: 'ord-002',
      userId: 'user123',
      items: [
        {
          productId: '7',
          name: 'Portable Bluetooth Speaker',
          price: 79.99,
          quantity: 1,
          imageUrl: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1',
        },
      ],
      total: 79.99,
      status: 'Shipped',
      createdAt: '2023-05-20',
    },
    {
      id: 'ord-003',
      userId: 'user123',
      items: [
        {
          productId: '8',
          name: 'Leather Laptop Bag',
          price: 89.99,
          quantity: 1,
          imageUrl: 'https://images.unsplash.com/photo-1547949003-9792a18a2601',
        },
        {
          productId: '3',
          name: 'Ergonomic Office Chair',
          price: 249.99,
          quantity: 1,
          imageUrl: 'https://images.unsplash.com/photo-1592078615290-033ee584e267',
        },
      ],
      total: 339.98,
      status: 'Processing',
      createdAt: '2023-06-10',
    },
  ];

  // If not logged in
  if (!user) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Card className="mx-auto max-w-lg">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl">Login Required</CardTitle>
            <CardDescription>
              Please login to view your orders
            </CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col items-center py-8">
            <Link to="/login">
              <Button className="mb-4">Login to Your Account</Button>
            </Link>
            <Link to="/products" className="text-sm text-gray-500 hover:text-gray-700">
              Or continue shopping
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  // If no orders
  if (!isLoading && !error && orders.length === 0) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Card className="mx-auto max-w-lg">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl">No Orders Yet</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col items-center py-8">
            <ShoppingBag className="h-16 w-16 text-gray-300 mb-4" />
            <p className="text-gray-500 mb-6">
              You haven't placed any orders yet.
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
      <h1 className="text-3xl font-bold mb-8">Your Orders</h1>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2].map((i) => (
            <div key={i} className="animate-pulse">
              <div className="h-10 bg-gray-200 rounded mb-4 w-1/3"></div>
              <div className="h-40 bg-gray-200 rounded mb-8"></div>
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="text-center py-8">
          <p className="text-red-500 mb-4">{error}</p>
          <Button onClick={() => window.location.reload()}>Try Again</Button>
        </div>
      ) : (
        <div className="space-y-8">
          {orders.map((order) => (
            <Card key={order.id} className="overflow-hidden">
              <CardHeader className="bg-gray-50">
                <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                  <div>
                    <CardTitle className="text-lg">
                      Order #{order.id}
                    </CardTitle>
                    <CardDescription>
                      Placed on {formatDate(order.createdAt)}
                    </CardDescription>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className={`px-3 py-1 text-sm rounded-full ${
                      order.status === 'Delivered'
                        ? 'bg-green-100 text-green-800'
                        : order.status === 'Shipped'
                        ? 'bg-blue-100 text-blue-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }`}>
                      {order.status}
                    </div>
                    <Button variant="outline" size="sm" asChild>
                      <Link to={`/orders/${order.id}`}>
                        Order Details
                        <ExternalLink className="ml-2 h-4 w-4" />
                      </Link>
                    </Button>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="pt-6">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Product</TableHead>
                      <TableHead>Price</TableHead>
                      <TableHead>Quantity</TableHead>
                      <TableHead>Total</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {order.items.map((item, index) => (
                      <TableRow key={`${order.id}-${item.productId}-${index}`}>
                        <TableCell>
                          <div className="flex items-center space-x-3">
                            {item.imageUrl ? (
                              <div className="flex-shrink-0 h-12 w-12 rounded overflow-hidden">
                                <img
                                  src={item.imageUrl}
                                  alt={item.name}
                                  className="h-full w-full object-cover"
                                />
                              </div>
                            ) : (
                              <div className="flex-shrink-0 h-12 w-12 rounded bg-gray-200 flex items-center justify-center">
                                <ShoppingBag className="h-6 w-6 text-gray-400" />
                              </div>
                            )}
                            <div>
                              <Link 
                                to={`/products/${item.productId}`}
                                className="font-medium hover:text-purple-600 transition-colors"
                              >
                                {item.name}
                              </Link>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell>${item.price.toFixed(2)}</TableCell>
                        <TableCell>{item.quantity}</TableCell>
                        <TableCell className="font-medium">
                          ${(item.price * item.quantity).toFixed(2)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                <div className="mt-6 flex justify-end">
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <div className="text-lg font-bold">
                      Total: ${order.total.toFixed(2)}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrdersPage;
