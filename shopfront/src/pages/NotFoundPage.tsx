
import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ShoppingBag, Home } from 'lucide-react';

const NotFoundPage = () => {
  return (
    <div className="min-h-[70vh] flex items-center justify-center">
      <div className="text-center px-4">
        <h1 className="text-9xl font-bold text-purple-600">404</h1>
        <h2 className="text-3xl font-semibold mt-4 mb-6">Page Not Found</h2>
        <p className="text-gray-600 max-w-md mx-auto mb-8">
          The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
        </p>
        <div className="flex flex-wrap justify-center gap-4">
          <Link to="/">
            <Button>
              <Home className="mr-2 h-4 w-4" />
              Back to Home
            </Button>
          </Link>
          <Link to="/products">
            <Button variant="outline">
              <ShoppingBag className="mr-2 h-4 w-4" />
              Browse Products
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default NotFoundPage;
