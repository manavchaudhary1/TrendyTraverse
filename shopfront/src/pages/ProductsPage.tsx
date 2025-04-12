
import React, { useState, useEffect } from 'react';
import { productApi } from '@/lib/api';
import ProductGrid from '@/components/products/ProductGrid';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Slider } from '@/components/ui/slider';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Search, Filter } from 'lucide-react';
import { useLocation, useNavigate } from 'react-router-dom';

const ProductsPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  // Extract the search term from URL query parameters
  const queryParams = new URLSearchParams(location.search);
  const initialSearchTerm = queryParams.get('keyword') || '';

  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usesFallback, setUsesFallback] = useState(false);
  
  // Filter states
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [priceRange, setPriceRange] = useState([0, 1000]);
  const [sortBy, setSortBy] = useState('newest');
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setIsLoading(true);
        setUsesFallback(false);
        // Use the search term as keyword for the API call
        const response = await productApi.getProducts(searchTerm);
        setProducts(response.data);
        setFilteredProducts(response.data);
      } catch (err) {
        console.error('Error fetching products:', err);
        setError('Failed to load products. Please try again later.');
        
        // Always use placeholder data if API isn't ready
        setProducts(placeholderProducts);
        setFilteredProducts(placeholderProducts);
        setUsesFallback(true);
      } finally {
        setIsLoading(false);
      }
    };

    fetchProducts();
  }, [searchTerm]);

  // Apply filters when filter states change (price and sorting)
  useEffect(() => {
    if (products.length === 0) return;
    
    let result = [...products];
    
    // Apply price filter
    result = result.filter(product => 
      product.price >= priceRange[0] && product.price <= priceRange[1]
    );
    
    // Apply sorting
    switch (sortBy) {
      case 'price-low':
        result.sort((a, b) => a.price - b.price);
        break;
      case 'price-high':
        result.sort((a, b) => b.price - a.price);
        break;
      case 'newest':
        // Assuming products have a dateAdded field, otherwise keep default
        break;
      case 'popularity':
        // Assuming products have a popularity or sales field
        break;
      default:
        break;
    }
    
    setFilteredProducts(result);
  }, [priceRange, sortBy, products]);

  // Also update URL when search form is submitted
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      // Update URL with search term
      navigate(`/products?keyword=${encodeURIComponent(searchTerm.trim())}`, { replace: true });
    }
  };

  // Update searchTerm when URL query parameter changes
  useEffect(() => {
    const keyword = queryParams.get('keyword') || '';
    setSearchTerm(keyword);
  }, [location.search]);

  const resetFilters = () => {
    setSearchTerm('');
    setPriceRange([0, 1000]);
    setSortBy('newest');
    // Also reset the URL
    navigate('/products', { replace: true });
  };

  // Placeholder products in case API isn't ready
  const placeholderProducts = [
    {
      id: '1',
      name: 'Premium Wireless Headphones',
      price: 129.99,
      imageUrls: ['https://images.unsplash.com/photo-1505740420928-5e560c06d30e'],
      brand: 'AudioTech',
      availabilityStatus: 'In Stock',
    },
    {
      id: '2',
      name: 'Ultra HD Smart TV 55"',
      price: 499.99,
      imageUrls: ['https://images.unsplash.com/photo-1593784991095-a205069470b6'],
      brand: 'VisionPro',
      availabilityStatus: 'In Stock',
    },
    {
      id: '3',
      name: 'Ergonomic Office Chair',
      price: 249.99,
      imageUrls: ['https://images.unsplash.com/photo-1592078615290-033ee584e267'],
      brand: 'ComfortPlus',
      availabilityStatus: 'In Stock',
    },
    {
      id: '4',
      name: 'Professional DSLR Camera',
      price: 899.99,
      imageUrls: ['https://images.unsplash.com/photo-1516035069371-29a1b244cc32'],
      brand: 'PhotoMaster',
      availabilityStatus: 'In Stock',
    },
    {
      id: '5',
      name: 'Stainless Steel Cookware Set',
      price: 199.99,
      imageUrls: ['https://images.unsplash.com/photo-1584454435222-9543a28334a8'],
      brand: 'ChefSelect',
      availabilityStatus: 'Limited Stock',
    },
    {
      id: '6',
      name: 'Fitness Smartwatch',
      price: 149.99,
      imageUrls: ['https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1'],
      brand: 'FitLife',
      availabilityStatus: 'In Stock',
    },
    {
      id: '7',
      name: 'Portable Bluetooth Speaker',
      price: 79.99,
      imageUrls: ['https://images.unsplash.com/photo-1608043152269-423dbba4e7e1'],
      brand: 'SoundWave',
      availabilityStatus: 'In Stock',
    },
    {
      id: '8',
      name: 'Leather Laptop Bag',
      price: 89.99,
      imageUrls: ['https://images.unsplash.com/photo-1547949003-9792a18a2601'],
      brand: 'UrbanGear',
      availabilityStatus: 'In Stock',
    },
  ];

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Page Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Shop All Products</h1>
        <p className="text-gray-600">
          Browse our collection of high-quality products.
        </p>
      </div>

      {/* Search and Filters */}
      <div className="mb-8">
        <div className="flex flex-col md:flex-row gap-4 mb-4">
          {/* Search Bar */}
          <form onSubmit={handleSearch} className="flex-grow">
            <div className="relative">
              <Input
                type="search"
                placeholder="Search products..."
                className="w-full pr-10"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <Button
                type="submit"
                variant="ghost"
                size="icon"
                className="absolute right-0 top-0"
              >
                <Search className="h-4 w-4" />
              </Button>
            </div>
          </form>

          {/* Sort Dropdown */}
          <div className="w-full md:w-48">
            <Select value={sortBy} onValueChange={setSortBy}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Sort by" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="newest">Newest</SelectItem>
                <SelectItem value="price-low">Price: Low to High</SelectItem>
                <SelectItem value="price-high">Price: High to Low</SelectItem>
                <SelectItem value="popularity">Popularity</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Filter Toggle Button (Mobile) */}
          <div className="md:hidden">
            <Button
              variant="outline"
              className="w-full"
              onClick={() => setShowFilters(!showFilters)}
            >
              <Filter className="mr-2 h-4 w-4" />
              {showFilters ? 'Hide Filters' : 'Show Filters'}
            </Button>
          </div>
        </div>

        {/* Filters (responsive) */}
        <div className={`${showFilters ? 'block' : 'hidden'} md:block bg-white p-4 rounded-lg border`}>
          <div className="flex flex-col md:flex-row justify-between gap-6">
            {/* Price Range Filter */}
            <div className="space-y-2 flex-grow">
              <h3 className="font-medium">Price Range</h3>
              <Slider
                defaultValue={[0, 1000]}
                max={1000}
                step={10}
                value={priceRange}
                onValueChange={setPriceRange}
                className="my-4"
              />
              <div className="flex justify-between text-sm text-gray-500">
                <span>${priceRange[0]}</span>
                <span>${priceRange[1]}</span>
              </div>
            </div>

            {/* Reset Button */}
            <div className="flex items-end">
              <Button
                variant="outline"
                onClick={resetFilters}
              >
                Reset Filters
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Products Count */}
      <div className="mb-6 flex justify-between items-center">
        <p className="text-sm text-gray-500">
          Showing {filteredProducts.length} of {products.length} products
        </p>
        
        {usesFallback && (
          <div className="text-yellow-600 text-sm font-medium">
            Showing sample products while connecting to the product service
          </div>
        )}
      </div>

      {/* Product Grid */}
      {error && !isLoading && !usesFallback ? (
        <div className="text-center py-10">
          <p className="text-red-500 mb-4">{error}</p>
          <Button onClick={() => window.location.reload()}>Try Again</Button>
        </div>
      ) : (
        <ProductGrid products={filteredProducts} isLoading={isLoading} />
      )}
    </div>
  );
};

export default ProductsPage;
