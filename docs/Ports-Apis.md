# Content
- [Ports](#ports)
- [API Documentation](#api-documentation)
  - [User Service APIs](#user-service-api)
  - [Product Service APIs](#product-service-api)
  - [Review Service APIs](#review-service-api)
  - [Cart Service APIs](#cart-service-api)
  - [Order Service APIs](#order-service-api)
- [Postman Collection](#postman-collection)

<a name="ports"></a>
# Ports
| Service Name                 | Port Number  |
|------------------------------|--------------|
| Config Server                | 8085         |
| Eureka Server                | 8761         |
| Gateway                      | 8072         |
| Product Service              | 8090         |
| Review Service               | 8091         |
| Cart Service                 | 8092         |
| Order Service                | 8093         |
| User Service                 | 8094         |
| Debugging for any service    | 5005         |
| Postgres for Service         | 5432         |
| Postgres for Keycloak        | 5433         |
| Keycloak                     | 8080         |
| Redis                        | 6379         |
| Kafka                        | 9092         |
| Kafka Controller             | 9093         |
| ElasticSearch                | 9200         |
| Logstash                     | 5000         |
| Kibana                       | 5601         |
| Zipkin                       | 9411         |
| Prometheus                   | 9090         |

<a name="api-documentation"></a>
# API Documentation

<a name="user-service-api"></a>
## User Service APIs

### Sign Up
**Endpoint:** `POST gatewayserver:8072/user-service/users/create`
<br/>**Description:** Creates a user.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/users/create`
- **Request Body**
  ```json
    {
    "username": "",
    "email": "",
    "password": ""
    }
  ```


### Get AccessToken
**Endpoint:** `GET gatewayserver:8072/user-service/users/token`
<br/>**Description:** Generates an access token for the user.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/users/token`
- **Request Body**
  ```json
    {
    "username": "",
    "password": ""
    }
  ```
  
### Refresh AccessToken
**Endpoint:** `GET gatewayserver:8072/user-service/users/refresh-token`
<br/>**Description:** Refreshes the access token for the user.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/users/refresh-token`
- **Request Body**
  ```json
    {
    "refreshToken": ""
    }
  ```
  
### Promote User
**Endpoint:** `POST gatewayserver:8072/user-service/users/promote/{userName}`
<br/>**Description:** Promotes a user to admin (Need **Admin** Auth).
<br/> 
#### Request
- **Method:** `POST`
- **URL:** `/users/promote/{userName}`
-  **Request Header**
    ``` 
   Authorization: Bearer {accessToken}
    ```

<a name="product-service-api"></a>
## Product Service APIs

### Get Product
**Endpoint:** `GET gatewayserver:8072/product-service/products/{productId}`
<br/>**Description:** Retrieves a product by its ID.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/products/{productId}`


### Create Product
**Endpoint:** `POST gatewayserver:8072/product-service/products`
<br/>**Description:** Creates a new product.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/products`
-  **Request Header**
  ``` 
 Authorization: Bearer {accessToken}
  ```
- **Request Body**{
  ```json
  {
    "name": "New Product",
    "brand": "New Brand",
    "imageUrls": [
    "https://example.com/image1.jpg",
    "https://example.com/image2.jpg"
    ],
    "featureBullets": [
    "New",
    "Solid",
    "Tight Tight Tight"
    ],
    "pricing": 99.99,
    "availabilityStatus": "In Stock"
  }
   ```

### Update Product
**Endpoint:** `PUT gatewayserver:8072/product-service/products/{productId}`
<br/>**Description:** Updates an existing product.
<br/>
#### Request
- **Method:** `PUT`
- **URL:** `/products/{productId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
- **Request Body**
  ```json
  {
    "name": "New Product 2",
    "brand": "Brand Name",
    "fullDescription": "Lund",
    "pricing": 99.99,
    "listPrice": null,
    "availabilityStatus": "In Stock",
    "productCategory": null,
    "productDimensions": null,
    "dateFirstAvailable": null,
    "manufacturer": "Jhaatu",
    "countryOfOrigin": "Wakanda",
    "averageRating": null,
    "totalReviews": null,
    "fiveStarReviews": null,
    "fourStarReviews": null,
    "threeStarReviews": null,
    "twoStarReviews": null,
    "oneStarReviews": null
  }
   ``` 

### Delete Product
**Endpoint:** `DELETE gatewayserver:8072/product-service/products/{productId}`
<br/>**Description:** Deletes a product by its ID.
<br/>
#### Request
- **Method:** `DELETE`
- **URL:** `/products/{productId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```


<a name="review-service-api"></a>
## Review Service APIs

### Get Reviews
**Endpoint:** `GET gatewayserver:8072/review-service/products/{reviewId}/reviews`
<br/>**Description:** Retrieves reviews for a product by its ID.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/products/{reviewId}/reviews`

### Create Review
**Endpoint:** `POST gatewayserver:8072/review-service/products/{productId}/reviews`
<br/>**Description:** Creates a new review for a product.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/products/{productId}/reviews`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
  - **Request Body**
    ```json
    {
      "stars": 5,
      "verifiedPurchase": false,
      "manufacturerReplied": false,
      "userId": "UUID",
      "title": "New Review 3",
      "reviewText": "Was Legit Tight",
      "totalFoundHelpful": 0,
      "images": [
      "https://m.media-amazon.com/images/I/81NugOUkohL.jpg",
      "https://m.media-amazon.com/images/I/81U+jBXDBeL.jpg"
      ]
    }
    ```

<a name="cart-service-api"></a>
## Cart Service APIs

### Get Cart
**Endpoint:** `GET gatewayserver:8072/cart-service/carts/{userId}`
<br/>**Description:** Retrieves the cart for a user by their ID.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/carts/{userId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```

### Add Item to Cart
**Endpoint:** `POST gatewayserver:8072/cart-service/carts/{userId}/items`
<br/>**Description:** Adds an item to the cart for a user.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/carts/{userId}/items`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
- **Request Body**
  ```json
  {
    "productId": "{productId}",
    "quantity": 1
  }
  ```

### Update Item Quantity
**Endpoint:** `PUT gatewayserver:8072/cart-service/carts/{userId}/items`
<br/>**Description:** Updates the quantity of an item in the cart for a user.
<br/>
#### Request
- **Method:** `PUT`
- **URL:** `/carts/{userId}/items`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
- **Request Body**
  ```json
  {
    "productId": "{productId}",
    "quantity": 2
  }
  ```

### Remove Item from Cart
**Endpoint:** `DELETE gatewayserver:8072/cart-service/carts/{userId}/items/{productId}`
<br/>**Description:** Removes an item from the cart for a user.
<br/>
#### Request
- **Method:** `DELETE`
- **URL:** `/carts/{userId}/items/{productId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```

### Archive Cart
**Endpoint:** `DELETE gatewayserver:8072/cart-service/carts/{userId}/checkout`
<br/>**Description:** Archives the cart for a user.
<br/>
#### Request
- **Method:** `DELETE`
- **URL:** `/carts/{userId}/checkout`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```

<a name="order-service-api"></a>
## Order Service APIs

### Place Cart Items as Order
**Endpoint:** `POST gatewayserver:8072/order-service/orders/{userId}/cart`
<br/>**Description:** Places the items in the cart as an order for a user.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/orders/{userId}/cart`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
  
### Get Order
**Endpoint:** `GET gatewayserver:8072/order-service/orders/{userId}`
<br/>**Description:** Retrieves the order for a user by their ID.
<br/>
#### Request
- **Method:** `GET`
- **URL:** `/orders/{userId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```

### Place Order 
**Endpoint:** `POST gatewayserver:8072/order-service/orders/{userId}`
<br/>**Description:** Places an order for a user.
<br/>
##### Request
- **Method:** `POST`
- **URL:** `/orders/{userId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
- **Request Body**
  ```json
  {
    "productId" : 2,
    "quantity" : 10
  }
  ```
  
### Delete Order
**Endpoint:** `DELETE gatewayserver:8072/order-service/orders/{userId}/{orderId}`
<br/>**Description:** Deletes an order for a user by its ID.
<br/>
#### Request
- **Method:** `DELETE`
- **URL:** `/orders/{userId}/{orderId}`
- **Request Header**
  ``` 
    Authorization: Bearer {accessToken}
  ```
  
<a name="postman-collection"></a>
# Postman Collection
The Postman collection for the APIs can be found [here](TrendyTraverse-API.postman_collection.json), just import them in Postman and setup local env to start running services.