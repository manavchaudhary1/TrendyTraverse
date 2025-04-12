
import React from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useCart } from '@/contexts/CartContext';
import { ShoppingCart } from 'lucide-react';

interface ProductCardProps {
  id: string;
  name: string;
  price: number;
  imageUrl: string;
  brand: string;
  availabilityStatus: string;
}

const ProductCard: React.FC<ProductCardProps> = ({
  id,
  name,
  price,
  imageUrl,
  brand,
  availabilityStatus,
}) => {
  const { addItem, isLoading } = useCart();

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault(); // Prevent navigation when clicking the button
    e.stopPropagation(); // Prevent event bubbling
    
    addItem({
      productId: id,
      name,
      price,
      quantity: 1,
      imageUrl,
    });
  };

  const isOutOfStock = availabilityStatus?.toLowerCase() === 'out of stock';

  return (
    <Card className="h-full flex flex-col overflow-hidden hover:shadow-lg transition-shadow duration-300">
      <Link to={`/products/${id}`} className="flex-grow">
        <div className="relative pb-[100%] overflow-hidden">
          <img
            src={imageUrl || 'https://placehold.co/300x300/e2e8f0/1e293b?text=No+Image'}
            alt={name}
            className="absolute top-0 left-0 w-full h-full object-cover transition-transform duration-300 hover:scale-105"
          />
        </div>
        <CardContent className="p-4">
          <p className="text-sm text-gray-500 mb-1">{brand}</p>
          <h3 className="font-semibold text-md line-clamp-2 mb-2">{name}</h3>
          <div className="flex justify-between items-center">
            <span className="font-bold">${price.toFixed(2)}</span>
            <span className={`text-sm ${isOutOfStock ? 'text-red-500' : 'text-green-500'}`}>
              {availabilityStatus}
            </span>
          </div>
        </CardContent>
      </Link>
      <CardFooter className="p-4 pt-0">
        <Button
          onClick={handleAddToCart}
          disabled={isLoading || isOutOfStock}
          className="w-full"
        >
          <ShoppingCart className="mr-2 h-4 w-4" />
          {isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
        </Button>
      </CardFooter>
    </Card>
  );
};

export default ProductCard;
