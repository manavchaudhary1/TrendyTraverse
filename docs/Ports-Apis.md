<h1 align="center">Ports and Apis</h1>

<details>
<summary><strong>Table&nbsp;of&nbsp;Contents</strong></summary>

- [Ports](#ports)
- [API Documentation](#api-documentation)
  - [User Service APIs](#user-service-api)
  - [Product Service APIs](#product-service-api)
  - [Review Service APIs](#review-service-api)
  - [Cart Service APIs](#cart-service-api)
  - [Order Service APIs](#order-service-api)
- [Postman Collection](#postman-collection)
</details>

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
- **Response Body**
 ```json
  {
  "message": "User created successfully"
  }
  ```


### Get AccessToken
**Endpoint:** `GET gatewayserver:8072/user-service/users/token`
<br/>**Description:** Generates an access token for the user.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/users/token`
- **Request Body**
  ```json
    {
    "username": "",
    "password": ""
    }
  ```
- **Response Body**
  ```json
  {
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsd25sZk1IakpvbFhsZ2R3ODF0NEtYeTlMRTlzdlVCbUoxMG9nWG5EbEpnIn0.eyJleHAiOjE3NDM3OTIzMzAsImlhdCI6MTc0Mzc5MjAzMCwianRpIjoiYmMzNTUyMmMtMWMyNS00NmQxLTk4OTQtYjU1YTU4NGJjMWFkIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3VzZXItcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNGYzOWVjMjItYzM3YS00N2E0LThhN2MtYmQ4NzFiNjRkZmMxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibWFuYXYiLCJzaWQiOiI4YWFjM2RlOC03N2M5LTQxZjctOTBjOC1iY2VmNWE3NWQwMjAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy11c2VyLXJlYWxtIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJtYW5hdiI6eyJyb2xlcyI6WyJuZXctdXNlciIsImFkbWluIiwiY3VzdG9tZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbjEiLCJlbWFpbCI6ImFkbWluMUBhZG1pbi5jb20ifQ.f4_yd1vQaEgjXI4PoJdPJS_q8QFEoTcEF0kVzd7N2GMHj_CL4GZAN5ASmyRv6bWDRAMlSOCPtBVbF-U9DiHcbewvwBNXcbRyXEU09kShzE7t4KFFvVSUB-au_rNCr_9rHgRFSau4GjuAIgKsrRaYuK32iqn9rl2TYEOkl3rkgD0BpEZueAwdlN1G3on8UWaUzxI-5RISK-dsb5Xhhr7e4EHJJqrHZ3yWv3WmXxjukfzc45WO7B8vtdnEYODRLSzNGLCR3unC1cmUop2GQDWQSt04bTEkEXjp5OKCaDY9cZ_bz8xo7X2IeDCo4rGzsby9JxCbS4kIHo_iwwHah_CGyQ",
  "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjNDZhYzAyNy1jMjk2LTRmNzYtYjk2ZS03Zjk3ZTQyZDFkYmQifQ.eyJleHAiOjE3NDM3OTM4MzAsImlhdCI6MTc0Mzc5MjAzMCwianRpIjoiN2NjYmI4NDgtOTU2ZS00NmRiLTkxYmMtMTYxYTIyYWFlYWE1IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3VzZXItcmVhbG0iLCJhdWQiOiJodHRwOi8va2V5Y2xvYWs6ODA4MC9yZWFsbXMvdXNlci1yZWFsbSIsInN1YiI6IjRmMzllYzIyLWMzN2EtNDdhNC04YTdjLWJkODcxYjY0ZGZjMSIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJtYW5hdiIsInNpZCI6IjhhYWMzZGU4LTc3YzktNDFmNy05MGM4LWJjZWY1YTc1ZDAyMCIsInNjb3BlIjoiYmFzaWMgd2ViLW9yaWdpbnMgcm9sZXMgYWNyIHByb2ZpbGUgZW1haWwifQ.Q48SKfGLLR1ugFgB0cxloXFXqPGj2iAGOfHct_XGJXpZ1BtTc_nKppJ6ysY4AO4F4hasLtK1jOqaHK1o34GZmw",
  "refresh_expires_in": 1800,
  "expires_in": 300
  }
  ```
  
### Refresh AccessToken
**Endpoint:** `GET gatewayserver:8072/user-service/users/refresh-token`
<br/>**Description:** Refreshes the access token for the user.
<br/>
#### Request
- **Method:** `POST`
- **URL:** `/users/refresh-token`
- **Request Body**
  ```json
    {
    "refreshToken": ""
    }
  ```
- **Response Body**
 ```json
   {
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsd25sZk1IakpvbFhsZ2R3ODF0NEtYeTlMRTlzdlVCbUoxMG9nWG5EbEpnIn0.eyJleHAiOjE3NDM3OTMwODIsImlhdCI6MTc0Mzc5Mjc4MiwianRpIjoiYzI5ZmIyYmMtMTA2ZC00ZTk4LWEwNzMtMTNmYzQ1YWY0OTAzIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3VzZXItcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNGYzOWVjMjItYzM3YS00N2E0LThhN2MtYmQ4NzFiNjRkZmMxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibWFuYXYiLCJzaWQiOiI0MDZlNmI4ZC00M2QxLTQyZDEtOGNmNC0wOGNhYWY5OWY3N2UiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy11c2VyLXJlYWxtIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJtYW5hdiI6eyJyb2xlcyI6WyJuZXctdXNlciIsImFkbWluIiwiY3VzdG9tZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbjEiLCJlbWFpbCI6ImFkbWluMUBhZG1pbi5jb20ifQ.yXoL5S0qwJAohuXQ-uYzj2c2PgcnB0XKYmdgGcgOCa9QMQgu9JzRSaoC2r1Ke5A66AwUUFAtHImx3eaG1LUsKcbxtbrGrm8w5RN7xJUtSD9SFlRfju7JreRztKnlVbpw365Oyj7CJ3v3tjcKv3aqYKM-J7EkK-662SNtrgmP4fTf8s0MQNCqohyTqIzKAgtOrbD-xhBLtYQ88kozNXaH5t-YzQgd_20KQX4FyPHERkLC4rRzZfwN9kAW7bW2l8EQNU2c--PMf_X8sor-yjZa7iGBgRtOuc2vLyFcXyryd5vkyYqxRmzzfBgDqwxunVKKVtt91Hl4UhoKprMPg60ayw",
    "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjNDZhYzAyNy1jMjk2LTRmNzYtYjk2ZS03Zjk3ZTQyZDFkYmQifQ.eyJleHAiOjE3NDM3OTQ1ODIsImlhdCI6MTc0Mzc5Mjc4MiwianRpIjoiN2QyODBmOTYtNGY1Yy00MDZkLTgzNWItZjIyYjU3ZjQzZTBhIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3VzZXItcmVhbG0iLCJhdWQiOiJodHRwOi8va2V5Y2xvYWs6ODA4MC9yZWFsbXMvdXNlci1yZWFsbSIsInN1YiI6IjRmMzllYzIyLWMzN2EtNDdhNC04YTdjLWJkODcxYjY0ZGZjMSIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJtYW5hdiIsInNpZCI6IjQwNmU2YjhkLTQzZDEtNDJkMS04Y2Y0LTA4Y2FhZjk5Zjc3ZSIsInNjb3BlIjoiYmFzaWMgd2ViLW9yaWdpbnMgcm9sZXMgYWNyIHByb2ZpbGUgZW1haWwifQ.VecCPEJjIilnWUogvwz3RgUzAB6D9JJqKHxAiEE6Vd36wx0hmBPLXMVoMWo3k77F6EQ6tMqilDbPqkIU3L4cdw",
    "refresh_expires_in": 1800,
    "expires_in": 300
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
- **Response Body**
```json
{
    "productId": 688,
    "name": "0",
    "brand": null,
    "productImages": [
        {
            "imageId": 4032,
            "imageUrl": ""
        }
    ],
    "fullDescription": null,
    "featureBullets": [],
    "pricing": 0.00,
    "listPrice": 0.00,
    "availabilityStatus": null,
    "productCategory": "0",
    "productDimensions": null,
    "dateFirstAvailable": "2000-01-01",
    "manufacturer": null,
    "countryOfOrigin": null,
    "averageRating": 4.1,
    "totalReviews": 0,
    "fiveStarReviews": 63,
    "fourStarReviews": 11,
    "threeStarReviews": 8,
    "twoStarReviews": 3,
    "oneStarReviews": 14,
    "reviews": [
        {
            "reviewId": 6237,
            "productId": 688,
            "stars": 5,
            "reviewDate": "2022-07-18",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "58801cbf-f60e-43a9-a204-bb4894ede7cd",
            "title": "5.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Kids love it!",
            "reviewText": "I have a 6 yr and 13 yr really enjoy this game.\n  \nRead more",
            "totalFoundHelpful": 138,
            "images": null
        },
        {
            "reviewId": 6238,
            "productId": 688,
            "stars": 4,
            "reviewDate": "2024-10-16",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": null,
            "title": "4.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Pretty good",
            "reviewText": "This is a good game but the ad said it was hard but it isn't really hard also the ads are anoying.\n  \nRead more",
            "totalFoundHelpful": 44,
            "images": null
        },
        {
            "reviewId": 6239,
            "productId": 688,
            "stars": 1,
            "reviewDate": "2024-12-27",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "965acaa2-c2eb-430a-8aa7-7732e6b97595",
            "title": "1.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Do not download!",
            "reviewText": "Hi! I think this game is a ripoff. Three reasons I think that are:1. You can't roll in the first round at all.2. It has way too many ads.3. It looks very fake and plastic.\n  \nRead more",
            "totalFoundHelpful": 0,
            "images": null
        },
        {
            "reviewId": 6240,
            "productId": 688,
            "stars": 2,
            "reviewDate": "2024-12-22",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "3dc52be5-9c1f-47f8-ad97-c48ea6bc4e42",
            "title": "2.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Ads, Ad Popup, and Meh",
            "reviewText": "There are a lot of ads AND the popup for the buy the version for no ads comes up after every single thing you do. It's annoying. The game itself isn't difficult to learn, but you can't go even remotely fast because you fly off of the track and have to start over. However, going slower makes the game way too easy. The game is just meh!\n  \nRead more",
            "totalFoundHelpful": 3,
            "images": null
        },
        {
            "reviewId": 6241,
            "productId": 688,
            "stars": 4,
            "reviewDate": "2024-11-01",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": null,
            "title": "4.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Meh",
            "reviewText": "I like it and everything except for the ads. The ads are sooo annoying\n  \nRead more",
            "totalFoundHelpful": 136,
            "images": null
        },
        {
            "reviewId": 6242,
            "productId": 688,
            "stars": 1,
            "reviewDate": "2024-11-29",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": null,
            "title": "1.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    This game sucks",
            "reviewText": "Wouldn't let me roll on the first level. This game is trash. Why can't you make a better game. Do better.\n  \nRead more",
            "totalFoundHelpful": 32,
            "images": null
        },
        {
            "reviewId": 6243,
            "productId": 688,
            "stars": 2,
            "reviewDate": "2024-11-30",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "e05df04c-99ad-49dc-991e-bd7402b81712",
            "title": "2.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    To easy and to many ads.",
            "reviewText": "Its fun but to easy and the ads take really long.\n  \nRead more",
            "totalFoundHelpful": 51,
            "images": null
        },
        {
            "reviewId": 6244,
            "productId": 688,
            "stars": 4,
            "reviewDate": "2023-03-04",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "cc2c1ba1-27bd-4b33-99e0-21f93558aeec",
            "title": "4.0 out of 5 stars\n\n\n\n\n\n\n\n  \n  \n    Fun but Low Gravity",
            "reviewText": "Fun but if you go to fast the ball flies off the track and it keeps happening. Also the adds are to much.\n  \nRead more",
            "totalFoundHelpful": 79,
            "images": null
        },
        {
            "reviewId": 6245,
            "productId": 688,
            "stars": 2,
            "reviewDate": "2024-11-10",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "37b0911c-3314-4cca-827f-ec5a96d0781a",
            "title": "malo",
            "reviewText": "es pura publicidad\n  \n  \nRead more",
            "totalFoundHelpful": 0,
            "images": null
        },
        {
            "reviewId": 6246,
            "productId": 688,
            "stars": 1,
            "reviewDate": "2024-11-30",
            "verifiedPurchase": false,
            "manufacturerReplied": false,
            "userId": "7648a2a6-1943-4af8-a4cf-23600d3b7d8c",
            "title": "Bad",
            "reviewText": "The second I joined I got an ad it wouldn't let me play. This might just be my device but in my opinion, this game is horrible.\n  \nRead more",
            "totalFoundHelpful": 0,
            "images": null
        }
    ]
}
```

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