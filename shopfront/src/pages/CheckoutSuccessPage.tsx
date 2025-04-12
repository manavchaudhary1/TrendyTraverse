
import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { CheckCircle, ShoppingBag } from 'lucide-react';

const CheckoutSuccessPage = () => {
  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-lg mx-auto text-center">
        <div className="mb-8">
          <CheckCircle className="h-16 w-16 text-green-500 mx-auto" />
        </div>
        <h1 className="text-3xl font-bold mb-4">Order Placed Successfully!</h1>
        <p className="text-lg text-gray-600 mb-8">
          Thank you for your purchase. Your order has been placed and is being processed.
        </p>
        
        <div className="bg-gray-50 p-6 rounded-lg mb-8">
          <h2 className="text-lg font-semibold mb-4">What's Next?</h2>
          <ul className="text-left space-y-3">
            <li className="flex items-start">
              <span className="bg-purple-100 text-purple-800 rounded-full w-6 h-6 flex items-center justify-center mr-2 flex-shrink-0">1</span>
              <span>You will receive an order confirmation email with details of your order.</span>
            </li>
            <li className="flex items-start">
              <span className="bg-purple-100 text-purple-800 rounded-full w-6 h-6 flex items-center justify-center mr-2 flex-shrink-0">2</span>
              <span>Once your order ships, we will send you a tracking number.</span>
            </li>
            <li className="flex items-start">
              <span className="bg-purple-100 text-purple-800 rounded-full w-6 h-6 flex items-center justify-center mr-2 flex-shrink-0">3</span>
              <span>You can view the status of your order at any time in your account.</span>
            </li>
          </ul>
        </div>
        
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link to="/orders">
            <Button variant="default">
              View Your Orders
            </Button>
          </Link>
          <Link to="/products">
            <Button variant="outline">
              <ShoppingBag className="mr-2 h-4 w-4" />
              Continue Shopping
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default CheckoutSuccessPage;
