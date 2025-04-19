classDiagram
direction BT
class CustomException {
  + CustomException(String) 
}
class GlobalExceptionHandler {
  + GlobalExceptionHandler() 
  + handleCustomException(CustomException) ResponseEntity~Map~String, String~~
  + handleGeneralException(Exception) ResponseEntity~Map~String, String~~
}
class JwtAuthConverter {
  + JwtAuthConverter() 
  - extractResourceRoles(Jwt) Collection~GrantedAuthority~
  + convert(Jwt) AbstractAuthenticationToken?
  - getPrincipalClaimName(Jwt) String
}
class Product {
  + Product() 
  + Product(Long, String, String, String, BigDecimal, BigDecimal, String, String, String, LocalDate, String, String, Double, Integer, Integer, Integer, Integer, Integer, Integer, List~ProductImage~, List~ProductFeatures~) 
  - String name
  - String productCategory
  - LocalDate dateFirstAvailable
  - String productDimensions
  - String manufacturer
  - Long productId
  - BigDecimal pricing
  - String countryOfOrigin
  - String availabilityStatus
  - Integer twoStarReviews
  - String brand
  - Integer fiveStarReviews
  - Integer oneStarReviews
  - List~ProductImage~ productImages
  - BigDecimal listPrice
  - List~ProductFeatures~ featureBullets
  - Integer fourStarReviews
  - Double averageRating
  - Integer totalReviews
  - Integer threeStarReviews
  - String fullDescription
  + toString() String
   String manufacturer
   Integer fiveStarReviews
   List~ProductFeatures~ featureBullets
   String brand
   Integer oneStarReviews
   BigDecimal listPrice
   String availabilityStatus
   String productCategory
   Integer fourStarReviews
   String name
   BigDecimal pricing
   Long productId
   Integer twoStarReviews
   Double averageRating
   Integer threeStarReviews
   Integer totalReviews
   List~ProductImage~ productImages
   String productDimensions
   String fullDescription
   LocalDate dateFirstAvailable
   String countryOfOrigin
}
class ProductController {
  + ProductController(ProductService) 
  + searchProducts(String) ResponseEntity~List~ProductSearchResultDTO~~
  + updateProduct(Long, ProductUpdateDTO) ResponseEntity~ProductResponseDTO~
  + createProduct(ProductCreateDTO) ResponseEntity~ProductResponseDTO~
  + deleteProduct(Long) ResponseEntity~ProductDeletionResponseDTO~
  + getProductById(Long) ResponseEntity~ProductResponseDTO~
}
class ProductControllerTest {
  ~ ProductControllerTest() 
  ~ searchProducts_WithKeyword_ReturnsSearchResults() void
  ~ setUp() void
  ~ createProduct_ReturnsCreatedProduct() void
  ~ deleteProduct_ReturnsDeletedProductInfo() void
  ~ updateProduct_ReturnsUpdatedProduct() void
  ~ getProductById_ReturnsProduct() void
  ~ searchProducts_WithEmptyKeyword_ReturnsAllProducts() void
  ~ searchProducts_WithNullKeyword_ReturnsAllProducts() void
}
class ProductCreateDTO {
  + ProductCreateDTO() 
  - String brand
  - LocalDate dateFirstAvailable
  - BigDecimal pricing
  - String productDimensions
  - String name
  - String availabilityStatus
  - BigDecimal listPrice
  - String manufacturer
  - List~String~ imageUrls
  - List~String~ featureBullets
  - String countryOfOrigin
  - String fullDescription
  - String productCategory
   String manufacturer
   List~String~ featureBullets
   String brand
   BigDecimal listPrice
   List~String~ imageUrls
   String availabilityStatus
   String productCategory
   String name
   BigDecimal pricing
   String productDimensions
   String fullDescription
   LocalDate dateFirstAvailable
   String countryOfOrigin
}
class ProductDeletionResponseDTO {
  + ProductDeletionResponseDTO() 
  - LocalDateTime deletionTimestamp
  - Long productId
  - String message
  - int reviewsDeleted
  - String productName
  - int imagesDeleted
  - int featuresDeleted
   Long productId
   int featuresDeleted
   String message
   int imagesDeleted
   LocalDateTime deletionTimestamp
   int reviewsDeleted
   String productName
}
class ProductFeatureDTO {
  + ProductFeatureDTO() 
  - Long featureId
  - String bullet
   String bullet
   Long featureId
}
class ProductFeatures {
  + ProductFeatures() 
  - String bullet
  - Integer featureId
  - Product product
  + toString() String
   Product product
   String bullet
   Integer featureId
}
class ProductFeaturesRepository {
<<Interface>>
  + findByProduct(Product) List~ProductFeatures~
  + deleteByProduct(Product) void
  + countByProduct(Product) int
}
class ProductImage {
  + ProductImage() 
  - String imageUrl
  - Integer imageId
  - Product product
  + toString() String
   Integer imageId
   Product product
   String imageUrl
}
class ProductImageDTO {
  + ProductImageDTO() 
  - Long imageId
  - String imageUrl
   Long imageId
   String imageUrl
}
class ProductImageRepository {
<<Interface>>
  + deleteByProduct(Product) void
  + findByProduct(Product) List~ProductImage~
  + countByProduct(Product) int
}
class ProductRedisRepository {
<<Interface>>

}
class ProductRepository {
<<Interface>>
  + searchProductsByKeyword(String) List~Object[]~
}
class ProductResponseDTO {
  + ProductResponseDTO() 
  - Long productId
  - Integer oneStarReviews
  - LocalDate dateFirstAvailable
  - String brand
  - BigDecimal pricing
  - List~ProductImageDTO~ productImages
  - BigDecimal listPrice
  - String availabilityStatus
  - Integer twoStarReviews
  - List~ReviewDTO~ reviews
  - String name
  - String productCategory
  - String manufacturer
  - String productDimensions
  - String countryOfOrigin
  - String fullDescription
  - Integer threeStarReviews
  - List~ProductFeatureDTO~ featureBullets
  - Integer fourStarReviews
  - Double averageRating
  - Integer fiveStarReviews
  - Integer totalReviews
   String manufacturer
   Integer fiveStarReviews
   List~ProductFeatureDTO~ featureBullets
   String brand
   Integer oneStarReviews
   BigDecimal listPrice
   String availabilityStatus
   String productCategory
   Integer fourStarReviews
   String name
   List~ReviewDTO~ reviews
   BigDecimal pricing
   Long productId
   Integer twoStarReviews
   Double averageRating
   Integer threeStarReviews
   List~ProductImageDTO~ productImages
   Integer totalReviews
   String productDimensions
   String fullDescription
   LocalDate dateFirstAvailable
   String countryOfOrigin
}
class ProductSearchResultDTO {
  + ProductSearchResultDTO() 
  + ProductSearchResultDTO(Long, String, BigDecimal, String) 
  - BigDecimal pricing
  - String name
  - String firstImage
  - Long productId
   String name
   BigDecimal pricing
   Long productId
   String firstImage
}
class ProductService {
  + ProductService(ProductRepository, ProductImageRepository, ProductFeaturesRepository, ReviewRestTemplateClient, ProductRedisRepository) 
  - checkRedisCache(Long) Product
  - cacheProductObject(Product) void
  + getProductById(Long) ProductResponseDTO
  + createProduct(ProductCreateDTO) ProductResponseDTO
  - randomlyRunLong() void
  - convertToDTO(Product) ProductResponseDTO
  - getReviews(Long) List~Review~
  - convertToFeatureDTO(ProductFeatures) ProductFeatureDTO
  + updateProduct(Long, ProductUpdateDTO) ProductResponseDTO
  ~ buildFallBackProduct(Long, Throwable) ProductResponseDTO
  - convertToImageDTO(ProductImage) ProductImageDTO
  + searchProductsByKeyword(String) List~ProductSearchResultDTO~
  - sleep() void
  - removeFromCache(Long) void
  - convertToReviewDTO(Review) ReviewDTO
  + deleteProduct(Long) ProductDeletionResponseDTO
}
class ProductServiceApplication {
  + ProductServiceApplication() 
  + main(String[]) void
}
class ProductServiceApplicationTests {
  ~ ProductServiceApplicationTests() 
  ~ contextLoads() void
}
class ProductServiceTest {
  ~ ProductServiceTest() 
  ~ createProduct_Success_ReturnsCreatedProduct() void
  ~ searchProductsByKeyword_ReturnsMatchingProducts() void
  ~ updateProduct_Success_ReturnsUpdatedProduct() void
  ~ searchProductsByKeyword_WithNullKeyword_ReturnsAllProducts() void
  ~ getProductById_NotFound_ThrowsException() void
  ~ setUp() void
  ~ searchProductsByKeyword_MappingException_HandlesGracefully() void
  ~ deleteProduct_NotFound_ThrowsException() void
  ~ getProductById_FromRepository_ReturnsProduct() void
  ~ deleteProduct_Success_ReturnsDeletedInfo() void
  ~ getProductById_FromCache_ReturnsProduct() void
  ~ updateProduct_NotFound_ThrowsException() void
  ~ createProduct_Exception_ThrowsResponseStatusException() void
}
class ProductUpdateDTO {
  + ProductUpdateDTO() 
  - BigDecimal pricing
  - List~String~ featureBullets
  - BigDecimal listPrice
  - String name
  - String countryOfOrigin
  - String availabilityStatus
  - String brand
  - String fullDescription
  - String productDimensions
  - String productCategory
  - LocalDate dateFirstAvailable
  - List~String~ imageUrls
  - String manufacturer
   String manufacturer
   List~String~ featureBullets
   String brand
   BigDecimal listPrice
   List~String~ imageUrls
   String availabilityStatus
   String productCategory
   String name
   BigDecimal pricing
   String productDimensions
   String fullDescription
   LocalDate dateFirstAvailable
   String countryOfOrigin
}
class RestTemplateConfig {
  + RestTemplateConfig() 
  + restTemplate() RestTemplate
}
class Review {
  + Review() 
  - Long reviewId
  - Long productId
  - String title
  - Boolean verifiedPurchase
  - LocalDate reviewDate
  - List~String~ images
  - Boolean manufacturerReplied
  - UUID userId
  - Integer totalFoundHelpful
  - Integer stars
  - String reviewText
  + toString() String
   Boolean verifiedPurchase
   Long productId
   String title
   Boolean manufacturerReplied
   Integer totalFoundHelpful
   Integer stars
   UUID userId
   String reviewText
   LocalDate reviewDate
   Long reviewId
   List~String~ images
}
class ReviewChangeHandler {
  + ReviewChangeHandler(ReviewRestTemplateClient) 
  + init() void
  + inboundReviewChange() Consumer~ReviewChangeModel~
  + errorHandler() Consumer~ErrorMessage~
}
class ReviewChangeModel {
  + ReviewChangeModel() 
  + ReviewChangeModel(String, String, Long, String) 
  - String action
  - Long productId
  - String correlationId
  - String type
   String action
   Long productId
   String type
   String correlationId
}
class ReviewDTO {
  + ReviewDTO() 
  - Boolean verifiedPurchase
  - Long reviewId
  - String title
  - Integer totalFoundHelpful
  - List~String~ images
  - Integer stars
  - LocalDate reviewDate
  - UUID userId
  - Boolean manufacturerReplied
  - String reviewText
  - Long productId
   Boolean verifiedPurchase
   Long productId
   String title
   Boolean manufacturerReplied
   Integer totalFoundHelpful
   Integer stars
   UUID userId
   String reviewText
   LocalDate reviewDate
   Long reviewId
   List~String~ images
}
class ReviewRedisRepository {
<<Interface>>
  + findByProductId(Long) List~Review~
}
class ReviewRestTemplateClient {
  + ReviewRestTemplateClient(RestTemplate, ReviewRedisRepository) 
  + getAllReviews(Long) List~Review~
  - checkRedisCache(Long) List~Review~
  + getReviewCount(Long) Integer
  - cacheReviewsList(Long, List~Review~) void
  + deleteAllReviews(Long) void
  + deleteCacheReviewsList(Long) void
   String accessToken
}
class SecurityConfig {
  + SecurityConfig(JwtAuthConverter) 
  + securityFilterChain(HttpSecurity) SecurityFilterChain
}
class ServiceConfig {
  + ServiceConfig() 
  - String redisServer
  - int redisPort
  + logRedisConfig() void
   int redisPort
   String redisServer
}

GlobalExceptionHandler  ..>  CustomException 
Product "1" *--> "featureBullets *" ProductFeatures 
Product "1" *--> "productImages *" ProductImage 
ProductController  ..>  ProductCreateDTO 
ProductController  ..>  ProductDeletionResponseDTO 
ProductController  ..>  ProductResponseDTO 
ProductController  ..>  ProductSearchResultDTO 
ProductController "1" *--> "productService 1" ProductService 
ProductController  ..>  ProductUpdateDTO 
ProductControllerTest "1" *--> "productController 1" ProductController 
ProductControllerTest "1" *--> "productCreateDTO 1" ProductCreateDTO 
ProductControllerTest "1" *--> "productDeletionResponseDTO 1" ProductDeletionResponseDTO 
ProductControllerTest "1" *--> "productResponseDTO 1" ProductResponseDTO 
ProductControllerTest "1" *--> "searchResults *" ProductSearchResultDTO 
ProductControllerTest "1" *--> "productService 1" ProductService 
ProductControllerTest "1" *--> "productUpdateDTO 1" ProductUpdateDTO 
ProductFeatures "1" *--> "product 1" Product 
ProductFeaturesRepository  ..>  Product 
ProductFeaturesRepository  ..>  ProductFeatures 
ProductImage "1" *--> "product 1" Product 
ProductImageRepository  ..>  Product 
ProductImageRepository  ..>  ProductImage 
ProductResponseDTO "1" *--> "featureBullets *" ProductFeatureDTO 
ProductResponseDTO "1" *--> "productImages *" ProductImageDTO 
ProductResponseDTO "1" *--> "reviews *" ReviewDTO 
ProductService  ..>  Product 
ProductService  ..>  ProductCreateDTO 
ProductService  ..>  ProductDeletionResponseDTO 
ProductService  ..>  ProductFeatureDTO 
ProductService  ..>  ProductFeatures 
ProductService "1" *--> "productFeaturesRepository 1" ProductFeaturesRepository 
ProductService  ..>  ProductImage 
ProductService  ..>  ProductImageDTO 
ProductService "1" *--> "productImageRepository 1" ProductImageRepository 
ProductService "1" *--> "productRedisRepository 1" ProductRedisRepository 
ProductService "1" *--> "productRepository 1" ProductRepository 
ProductService  ..>  ProductResponseDTO 
ProductService  ..>  ProductSearchResultDTO 
ProductService  ..>  ProductUpdateDTO 
ProductService  ..>  Review 
ProductService  ..>  ReviewDTO 
ProductService "1" *--> "reviewRestTemplateClient 1" ReviewRestTemplateClient 
ProductServiceTest "1" *--> "testProduct 1" Product 
ProductServiceTest "1" *--> "productCreateDTO 1" ProductCreateDTO 
ProductServiceTest "1" *--> "testFeatures *" ProductFeatures 
ProductServiceTest "1" *--> "productFeaturesRepository 1" ProductFeaturesRepository 
ProductServiceTest "1" *--> "testImages *" ProductImage 
ProductServiceTest "1" *--> "productImageRepository 1" ProductImageRepository 
ProductServiceTest "1" *--> "productRedisRepository 1" ProductRedisRepository 
ProductServiceTest "1" *--> "productRepository 1" ProductRepository 
ProductServiceTest "1" *--> "expectedResponseDTO 1" ProductResponseDTO 
ProductServiceTest "1" *--> "productService 1" ProductService 
ProductServiceTest "1" *--> "productUpdateDTO 1" ProductUpdateDTO 
ProductServiceTest "1" *--> "testReviews *" Review 
ProductServiceTest "1" *--> "reviewRestTemplateClient 1" ReviewRestTemplateClient 
ReviewChangeHandler  ..>  ReviewChangeModel 
ReviewChangeHandler "1" *--> "reviewRestTemplateClient 1" ReviewRestTemplateClient 
ReviewRedisRepository  ..>  Review 
ReviewRestTemplateClient  ..>  Review 
ReviewRestTemplateClient "1" *--> "reviewRedisRepository 1" ReviewRedisRepository 
SecurityConfig "1" *--> "jwtAuthConverter 1" JwtAuthConverter 
