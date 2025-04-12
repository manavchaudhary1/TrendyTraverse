import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { productApi } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import ProductGrid from '@/components/products/ProductGrid';
import { ArrowRight, Truck, RefreshCw, Shield, Award, Search } from 'lucide-react';
import { toast } from 'sonner';

const HomePage = () => {
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usesFallback, setUsesFallback] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

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

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setIsLoading(true);
        setUsesFallback(false);
        console.log("Attempting to fetch products...");
        const response = await productApi.getProducts();
        console.log("Products fetched successfully:", response);
        setFeaturedProducts(response.data.slice(0, 8));
      } catch (err) {
        console.error('Error fetching products:', err);
        setError('Failed to load products. Please try again later.');
        
        setFeaturedProducts(placeholderProducts);
        setUsesFallback(true);
        toast.error('Unable to connect to product service. Showing sample products instead.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/products?keyword=${encodeURIComponent(searchTerm.trim())}`);
    }
  };

  const displayProducts = featuredProducts.length > 0 ? featuredProducts : placeholderProducts;

  return (
    <div className="flex flex-col min-h-screen">
      <section className="relative bg-gradient-to-r from-purple-700 to-indigo-800 text-white">
        <div className="container mx-auto px-4 py-16 md:py-24">
          <div className="flex flex-col md:flex-row items-center">
            <div className="md:w-1/2 mb-10 md:mb-0">
              <h1 className="text-4xl md:text-5xl font-bold mb-4">Discover Trendy Products</h1>
              <p className="text-lg md:text-xl mb-8 opacity-90">
                Explore our collection of high-quality products at competitive prices.
                Shop with confidence and enjoy fast delivery.
              </p>
              <form onSubmit={handleSearch} className="mb-6">
                <div className="flex max-w-md">
                  <Input
                    type="text"
                    placeholder="Search for products..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="bg-white/10 text-white placeholder:text-white/60 border-white/20"
                  />
                  <Button type="submit" className="ml-2 bg-white text-purple-700 hover:bg-white/90">
                    <Search className="h-4 w-4" />
                  </Button>
                </div>
              </form>
              <div className="flex flex-wrap gap-4">
                <Link to="/products">
                  <Button size="lg" variant="default" className="bg-white text-purple-700 hover:bg-gray-100">
                    Shop Now
                    <ArrowRight className="ml-2 h-5 w-5" />
                  </Button>
                </Link>
                <Link to="/new-arrivals">
                  <Button size="lg" variant="outline" className="border-white text-white hover:bg-white/10">
                    New Arrivals
                  </Button>
                </Link>
              </div>
            </div>
            <div className="md:w-1/2">
              <img 
                src="https://images.unsplash.com/photo-1472851294608-062f824d29cc"
                alt="Shopping Experience"
                className="rounded-lg shadow-xl"
              />
            </div>
          </div>
        </div>
      </section>

      <section className="py-12 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 text-center">
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <Truck className="h-10 w-10 mx-auto mb-4 text-purple-600" />
              <h3 className="text-lg font-semibold mb-2">Free Shipping</h3>
              <p className="text-gray-600">On orders over $50</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <RefreshCw className="h-10 w-10 mx-auto mb-4 text-purple-600" />
              <h3 className="text-lg font-semibold mb-2">Easy Returns</h3>
              <p className="text-gray-600">30-day return policy</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <Shield className="h-10 w-10 mx-auto mb-4 text-purple-600" />
              <h3 className="text-lg font-semibold mb-2">Secure Payment</h3>
              <p className="text-gray-600">Protected checkout</p>
            </div>
            <div className="p-6 bg-white rounded-lg shadow-sm">
              <Award className="h-10 w-10 mx-auto mb-4 text-purple-600" />
              <h3 className="text-lg font-semibold mb-2">Quality Products</h3>
              <p className="text-gray-600">Curated selection</p>
            </div>
          </div>
        </div>
      </section>

      <section className="py-12">
        <div className="container mx-auto px-4">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold">Featured Products</h2>
            <Link to="/products">
              <Button variant="link" className="text-purple-600">
                View All <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </Link>
          </div>

          {usesFallback && (
            <div className="mb-4 p-3 bg-yellow-50 text-yellow-700 rounded-md">
              <p>Showing sample products while connecting to the product service...</p>
            </div>
          )}

          {error && !isLoading && !usesFallback ? (
            <div className="text-center py-10">
              <p className="text-red-500 mb-4">{error}</p>
              <Button onClick={() => window.location.reload()}>
                Try Again
              </Button>
            </div>
          ) : (
            <ProductGrid products={displayProducts} isLoading={isLoading} />
          )}
        </div>
      </section>

      <section className="bg-purple-600 text-white py-12">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-2xl md:text-3xl font-bold mb-4">Join Our Newsletter</h2>
          <p className="text-lg mb-8 max-w-2xl mx-auto">
            Subscribe to get special offers, free giveaways, and product announcements.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 max-w-md mx-auto">
            <input
              type="email"
              placeholder="Your email address"
              className="px-4 py-2 rounded-md flex-grow text-gray-900"
            />
            <Button className="bg-white text-purple-600 hover:bg-gray-100">
              Subscribe
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
