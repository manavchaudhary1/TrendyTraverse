
import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { productApi, reviewApi } from '@/lib/api';
import { ProductDTO } from '@/lib/entities/Product';
import { ReviewDTO, ReviewData } from '@/lib/entities/Review';
import { useCart } from '@/contexts/CartContext';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { 
  Card, 
  CardContent 
} from '@/components/ui/card';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose
} from "@/components/ui/dialog";
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs';
import { 
  ShoppingCart, 
  Star, 
  Check, 
  Truck, 
  RefreshCw,
  Shield 
} from 'lucide-react';
import { useToast } from '@/hooks/use-toast';

interface ProductDetailsType {
  id: string;
  name: string;
  brand: string;
  imageUrls: string[];
  price: number;
  availabilityStatus: string;
  fullDescription?: string;
  featureBullets?: string[];
  manufacturer?: string;
  countryOfOrigin?: string;
  productCategory?: string;
  averageRating?: number;
  totalReviews?: number;
}

interface ReviewType {
  id: string;
  userId: string;
  stars: number;
  title: string;
  reviewText: string;
  verifiedPurchase: boolean;
  totalFoundHelpful: number;
  images?: string[];
  username?: string;
  createdAt: string;
}

const ProductDetailPage = () => {
  const { productId } = useParams<{ productId: string }>();
  const [product, setProduct] = useState<ProductDetailsType | null>(null);
  const [reviews, setReviews] = useState<ReviewType[]>([]);
  const [selectedImage, setSelectedImage] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [isLoading, setIsLoading] = useState(true);
  const [isAddingToCart, setIsAddingToCart] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState("description");
  const [reviewDialogOpen, setReviewDialogOpen] = useState(false);
  const [reviewTitle, setReviewTitle] = useState('');
  const [reviewText, setReviewText] = useState('');
  const [reviewRating, setReviewRating] = useState(5);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);
  
  const { addItem } = useCart();
  const { user } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProductAndReviews = async () => {
      try {
        setIsLoading(true);
        setError(null);
        
        if (!productId) {
          setError('Product ID is missing');
          return;
        }

        const productResponse = await productApi.getProduct(productId);
        const productData: ProductDTO = productResponse.data;
        
        // Process feature bullets to handle both string and object formats
        const processedFeatureBullets = productData.featureBullets?.map(feature => {
          if (typeof feature === 'string') {
            return feature;
          } else if (feature && typeof feature === 'object') {
            // Safely check if the object has a 'bullet' property using type assertions
            const featureObj = feature as Record<string, any>;
            return featureObj && 'bullet' in featureObj 
              ? String(featureObj.bullet)
              : String(feature);
          }
          return String(feature); // Convert to string as fallback
        });
        
        const mappedProduct: ProductDetailsType = {
          id: productId || productData.productId.toString(),
          name: productData.name,
          brand: productData.brand || 'Unknown',
          imageUrls: productData.productImages?.map(img => img.imageUrl) || [],
          price: productData.pricing,
          availabilityStatus: productData.availabilityStatus || 'Unknown',
          fullDescription: productData.fullDescription || undefined,
          featureBullets: processedFeatureBullets,
          manufacturer: productData.manufacturer || undefined,
          countryOfOrigin: productData.countryOfOrigin || undefined,
          productCategory: productData.productCategory,
          averageRating: productData.averageRating,
          totalReviews: productData.totalReviews
        };
        
        setProduct(mappedProduct);
        if (mappedProduct.imageUrls && mappedProduct.imageUrls.length > 0) {
          setSelectedImage(mappedProduct.imageUrls[0]);
        }
        
        try {
          const reviewsResponse = await reviewApi.getReviews(productId);
          const reviewsData: ReviewDTO[] = reviewsResponse.data;
          
          const mappedReviews: ReviewType[] = reviewsData.map(review => ({
            id: review.reviewId.toString(),
            userId: review.userId || 'anonymous',
            stars: review.stars,
            title: review.title,
            reviewText: review.reviewText,
            verifiedPurchase: review.verifiedPurchase,
            totalFoundHelpful: review.totalFoundHelpful,
            images: review.images || undefined,
            username: review.userId || 'Anonymous',
            createdAt: review.reviewDate
          }));
          
          setReviews(mappedReviews);
        } catch (reviewErr) {
          console.error('Error fetching reviews:', reviewErr);
          // Continue with product display even if reviews fail
          setReviews(placeholderReviews);
        }
      } catch (err) {
        console.error('Error fetching product details:', err);
        setError('Failed to load product information. Please try again.');
        
        if (placeholderProduct.id === productId) {
          setProduct(placeholderProduct);
          setSelectedImage(placeholderProduct.imageUrls[0]);
          setReviews(placeholderReviews);
        }
      } finally {
        setIsLoading(false);
      }
    };

    if (productId) {
      fetchProductAndReviews();
    }
  }, [productId]);

  const handleAddToCart = async () => {
    if (!product) return;
    
    try {
      setIsAddingToCart(true);
      
      await addItem({
        productId: product.id,
        name: product.name,
        price: product.price,
        quantity: quantity,
        imageUrl: product.imageUrls?.[0] || '',
      });
    } catch (error) {
      console.error('Error adding to cart:', error);
    } finally {
      setIsAddingToCart(false);
    }
  };

  const handleSubmitReview = async () => {
    if (!user || !productId) {
      toast({
        variant: "destructive",
        title: "Authentication required",
        description: "Please log in to submit a review",
      });
      navigate('/login');
      return;
    }

    if (!reviewTitle || !reviewText) {
      toast({
        variant: "destructive",
        title: "Review incomplete",
        description: "Please provide both a title and review text",
      });
      return;
    }

    try {
      setIsSubmittingReview(true);
      
      const reviewData: ReviewData = {
        stars: reviewRating,
        title: reviewTitle,
        reviewText: reviewText
      };
      
      await reviewApi.createReview(productId, reviewData);
      
      // Add the new review to the list
      const newReview: ReviewType = {
        id: Date.now().toString(), // Temporary ID until refresh
        userId: user.id,
        stars: reviewRating,
        title: reviewTitle,
        reviewText: reviewText,
        verifiedPurchase: true,
        totalFoundHelpful: 0,
        username: user.username,
        createdAt: new Date().toISOString()
      };
      
      setReviews(prev => [newReview, ...prev]);
      
      // Clear form and close dialog
      setReviewTitle('');
      setReviewText('');
      setReviewRating(5);
      setReviewDialogOpen(false);
      
      toast({
        title: "Review submitted",
        description: "Thank you for your feedback!",
      });
      
      // Switch to the reviews tab
      setActiveTab("reviews");
      
    } catch (error) {
      console.error('Error submitting review:', error);
      toast({
        variant: "destructive",
        title: "Submission failed",
        description: "Unable to submit your review. Please try again later.",
      });
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const incrementQuantity = () => setQuantity(prev => prev + 1);
  const decrementQuantity = () => setQuantity(prev => (prev > 1 ? prev - 1 : 1));

  const renderStars = (rating: number) => {
    return Array(5)
      .fill(0)
      .map((_, i) => (
        <Star
          key={i}
          className={`h-4 w-4 ${
            i < rating ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300'
          }`}
        />
      ));
  };

  const placeholderProduct: ProductDetailsType = {
    id: '1',
    name: 'Premium Wireless Noise-Cancelling Headphones',
    brand: 'AudioTech',
    imageUrls: [
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e',
      'https://images.unsplash.com/photo-1577174881658-0f30ed549adc',
      'https://images.unsplash.com/photo-1546435770-a3e426bf472b',
    ],
    price: 29999,
    availabilityStatus: 'In Stock',
    fullDescription: 'Experience premium sound quality with our latest noise-cancelling headphones. Perfect for music lovers and professionals seeking immersive audio experiences. These headphones feature advanced Bluetooth 5.0 technology, 40-hour battery life, and comfortable over-ear design with memory foam ear cushions.',
    featureBullets: [
      'Active noise cancellation technology',
      'Up to 40 hours of battery life',
      'Premium memory foam ear cushions',
      'Voice assistant compatible',
      'Foldable design for easy storage',
    ],
    manufacturer: 'AudioTech Electronics',
    countryOfOrigin: 'Japan',
    productCategory: 'Electronics > Audio > Headphones',
    averageRating: 4.7,
    totalReviews: 243,
  };

  const placeholderReviews: ReviewType[] = [
    {
      id: 'r1',
      userId: 'u1',
      stars: 5,
      title: "Best headphones I've ever owned",
      reviewText: "The sound quality is incredible and the noise cancellation works perfectly for my daily commute. Battery life is also impressive, lasting me the entire week with daily use.",
      verifiedPurchase: true,
      totalFoundHelpful: 42,
      username: 'AudioEnthusiast',
      createdAt: '2023-06-15',
    },
    {
      id: 'r2',
      userId: 'u2',
      stars: 4,
      title: 'Great sound, slightly tight fit',
      reviewText: 'Sound quality is excellent and the noise cancellation works well. My only complaint is they feel a bit tight on my head after a few hours of use. Otherwise excellent product.',
      verifiedPurchase: true,
      totalFoundHelpful: 18,
      username: 'MusicLover',
      createdAt: '2023-05-22',
    },
    {
      id: 'r3',
      userId: 'u3',
      stars: 5,
      title: 'Perfect for work from home',
      reviewText: 'These headphones have been a lifesaver for working from home. The noise cancellation blocks out all household distractions and the microphone quality for calls is excellent.',
      verifiedPurchase: true,
      totalFoundHelpful: 27,
      images: ['https://images.unsplash.com/photo-1484704849700-f032a568e944'],
      username: 'RemoteWorker',
      createdAt: '2023-04-10',
    },
  ];

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-1/3 mb-4"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="aspect-square bg-gray-200 rounded"></div>
            <div className="space-y-4">
              <div className="h-6 bg-gray-200 rounded w-3/4"></div>
              <div className="h-4 bg-gray-200 rounded w-1/2"></div>
              <div className="h-10 bg-gray-200 rounded w-1/4"></div>
              <div className="h-12 bg-gray-200 rounded w-full"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center py-10">
          <h2 className="text-2xl font-bold text-red-500 mb-4">
            {error || 'Product not found'}
          </h2>
          <Button onClick={() => window.location.reload()}>
            Try Again
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="space-y-4">
          <div className="aspect-square bg-gray-100 rounded-lg overflow-hidden">
            <img
              src={selectedImage || product.imageUrls?.[0]}
              alt={product.name}
              className="w-full h-full object-contain"
            />
          </div>
          
          {product.imageUrls && product.imageUrls.length > 1 && (
            <div className="flex space-x-2 overflow-x-auto">
              {product.imageUrls.map((img, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImage(img)}
                  onMouseEnter={() => setSelectedImage(img)}
                  className={`w-20 h-20 rounded-md overflow-hidden border-2 ${
                    selectedImage === img ? 'border-purple-600' : 'border-transparent'
                  }`}
                >
                  <img
                    src={img}
                    alt={`${product.name} - view ${index + 1}`}
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="space-y-6">
          <div>
            <p className="text-gray-500 mb-1">{product.brand}</p>
            <h1 className="text-3xl font-bold mb-2">{product.name}</h1>
            
            {product.averageRating && (
              <div className="flex items-center mb-2">
                <div className="flex mr-2">
                  {renderStars(product.averageRating)}
                </div>
                <span className="text-sm text-gray-500">
                  {product.averageRating.toFixed(1)} ({product.totalReviews} reviews)
                </span>
              </div>
            )}
            
            <p className="text-2xl font-bold text-purple-600">
              ${(product.price / 100).toFixed(2)}
            </p>
          </div>

          <div className="border-t border-b py-4 my-4">
            <p className={`flex items-center ${
              product.availabilityStatus.toLowerCase() === 'in stock'
                ? 'text-green-600'
                : product.availabilityStatus.toLowerCase() === 'out of stock'
                ? 'text-red-500'
                : 'text-orange-500'
            }`}>
              {product.availabilityStatus.toLowerCase() === 'in stock' && (
                <Check className="mr-2 h-5 w-5" />
              )}
              {product.availabilityStatus}
            </p>
          </div>

          <div className="space-y-4">
            <div className="flex items-center space-x-2">
              <Button
                variant="outline"
                size="icon"
                onClick={decrementQuantity}
                disabled={quantity <= 1}
              >
                -
              </Button>
              <span className="w-12 text-center">{quantity}</span>
              <Button
                variant="outline"
                size="icon"
                onClick={incrementQuantity}
              >
                +
              </Button>
            </div>

            <Button
              onClick={handleAddToCart}
              disabled={
                isAddingToCart ||
                product.availabilityStatus.toLowerCase() === 'out of stock'
              }
              className="w-full"
              size="lg"
            >
              <ShoppingCart className="mr-2 h-5 w-5" />
              {isAddingToCart ? 'Adding...' : 'Add to Cart'}
            </Button>
          </div>

          <div className="space-y-3 pt-4">
            <div className="flex items-start space-x-3">
              <Truck className="h-5 w-5 text-gray-500 mt-0.5" />
              <div>
                <p className="font-medium">Free Shipping</p>
                <p className="text-sm text-gray-500">For orders over $50</p>
              </div>
            </div>
            <div className="flex items-start space-x-3">
              <RefreshCw className="h-5 w-5 text-gray-500 mt-0.5" />
              <div>
                <p className="font-medium">Easy Returns</p>
                <p className="text-sm text-gray-500">30-day return policy</p>
              </div>
            </div>
            <div className="flex items-start space-x-3">
              <Shield className="h-5 w-5 text-gray-500 mt-0.5" />
              <div>
                <p className="font-medium">Secure Checkout</p>
                <p className="text-sm text-gray-500">Safe & encrypted payment processing</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="mt-12">
        <Tabs defaultValue="description" value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="description">Description</TabsTrigger>
            <TabsTrigger value="features">Features</TabsTrigger>
            <TabsTrigger value="reviews">Reviews</TabsTrigger>
          </TabsList>
          
          <TabsContent value="description" className="py-4">
            <div className="prose max-w-none">
              <p className="text-gray-700 whitespace-pre-line">
                {product.fullDescription || 'No description available.'}
              </p>
              
              {(product.manufacturer || product.countryOfOrigin) && (
                <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                  {product.manufacturer && (
                    <div>
                      <h4 className="font-medium text-gray-900">Manufacturer</h4>
                      <p className="text-gray-600">{product.manufacturer}</p>
                    </div>
                  )}
                  {product.countryOfOrigin && (
                    <div>
                      <h4 className="font-medium text-gray-900">Country of Origin</h4>
                      <p className="text-gray-600">{product.countryOfOrigin}</p>
                    </div>
                  )}
                </div>
              )}
            </div>
          </TabsContent>
          
          <TabsContent value="features" className="py-4">
            {product.featureBullets && product.featureBullets.length > 0 ? (
              <ul className="list-disc pl-5 space-y-2">
                {product.featureBullets.map((feature, index) => (
                  <li key={index} className="text-gray-700">{feature}</li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-500">No features available.</p>
            )}
          </TabsContent>
          
          <TabsContent value="reviews" className="py-4">
            <div className="space-y-6">
              <div className="flex justify-between items-center">
                <h3 className="text-xl font-semibold">
                  Customer Reviews ({reviews.length})
                </h3>
                
                <Button 
                  variant="outline" 
                  onClick={() => {
                    if (!user) {
                      toast({
                        title: "Login required",
                        description: "Please login to write a review",
                        variant: "destructive"
                      });
                      navigate('/login');
                    } else {
                      setReviewDialogOpen(true);
                    }
                  }}
                >
                  Write a Review
                </Button>
              </div>
              
              {reviews.length > 0 ? (
                <div className="space-y-4">
                  {reviews.map((review) => (
                    <Card key={review.id}>
                      <CardContent className="p-4">
                        <div className="flex justify-between mb-2">
                          <div className="flex items-center">
                            <div className="flex mr-2">
                              {renderStars(review.stars)}
                            </div>
                            <h4 className="font-semibold">{review.title}</h4>
                          </div>
                          <span className="text-sm text-gray-500">
                            {new Date(review.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        
                        <p className="text-gray-600 mb-3">{review.reviewText}</p>
                        
                        {review.images && review.images.length > 0 && (
                          <div className="flex space-x-2 mt-3 mb-3">
                            {review.images.map((img, idx) => (
                              <div key={idx} className="w-16 h-16 rounded overflow-hidden">
                                <img
                                  src={img}
                                  alt={`Review image ${idx + 1}`}
                                  className="w-full h-full object-cover"
                                />
                              </div>
                            ))}
                          </div>
                        )}
                        
                        <div className="flex justify-between items-center mt-4">
                          <div className="text-sm">
                            <span className="font-medium">
                              {review.username || 'Anonymous'}
                            </span>
                            {review.verifiedPurchase && (
                              <span className="ml-2 text-green-600 text-xs">
                                Verified Purchase
                              </span>
                            )}
                          </div>
                          
                          <div className="text-sm text-gray-500">
                            {review.totalFoundHelpful} found this helpful
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              ) : (
                <div className="text-center py-6">
                  <p className="text-gray-500 mb-4">No reviews yet</p>
                  {user ? (
                    <Button onClick={() => setReviewDialogOpen(true)}>
                      Be the first to review this product!
                    </Button>
                  ) : (
                    <p>
                      <Link to="/login" className="text-purple-600 hover:underline">
                        Log in
                      </Link>{' '}
                      to leave a review.
                    </p>
                  )}
                </div>
              )}
            </div>
          </TabsContent>
        </Tabs>
      </div>

      {/* Review Dialog */}
      <Dialog open={reviewDialogOpen} onOpenChange={setReviewDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Write a Review</DialogTitle>
            <DialogDescription>
              Share your experience with this product
            </DialogDescription>
          </DialogHeader>
          
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="rating">Rating</Label>
              <div className="flex space-x-1">
                {[1, 2, 3, 4, 5].map((rating) => (
                  <button
                    key={rating}
                    type="button"
                    onClick={() => setReviewRating(rating)}
                    className="focus:outline-none"
                  >
                    <Star
                      className={`h-6 w-6 ${
                        rating <= reviewRating
                          ? 'text-yellow-500 fill-yellow-500'
                          : 'text-gray-300'
                      }`}
                    />
                  </button>
                ))}
              </div>
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="title">Review Title</Label>
              <Input
                id="title"
                value={reviewTitle}
                onChange={(e) => setReviewTitle(e.target.value)}
                placeholder="Summarize your experience"
                required
              />
            </div>
            
            <div className="space-y-2">
              <Label htmlFor="review">Your Review</Label>
              <Textarea
                id="review"
                value={reviewText}
                onChange={(e) => setReviewText(e.target.value)}
                placeholder="What did you like or dislike about this product?"
                rows={5}
                required
              />
            </div>
          </div>
          
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Cancel</Button>
            </DialogClose>
            <Button 
              onClick={handleSubmitReview} 
              disabled={isSubmittingReview || !reviewTitle || !reviewText}
            >
              {isSubmittingReview ? 'Submitting...' : 'Submit Review'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ProductDetailPage;
