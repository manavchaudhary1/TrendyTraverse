-- Users table remains unchanged as UUIDs are auto-generated
CREATE TABLE "users" (
    "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    "username" TEXT NOT NULL UNIQUE,
    "role" VARCHAR(255) CHECK ("role" IN ('admin', 'customer')) NOT NULL,
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE,
    "last_login" TIMESTAMP(0) WITHOUT TIME ZONE
);

-- Products Table (product_id set to auto-increment)
CREATE TABLE "products" (
    "product_id" SERIAL PRIMARY KEY,
    "name" TEXT NOT NULL,
    "brand" TEXT,
    "full_description" TEXT,
    "pricing" DECIMAL(10, 2),
    "list_price" DECIMAL(10, 2),
    "availability_status" TEXT,
    "product_category" TEXT,
    "product_dimensions" TEXT,
    "date_first_available" DATE,
    "manufacturer" TEXT,
    "country_of_origin" TEXT,
    "average_rating" DECIMAL(3, 2),
    "total_reviews" INTEGER,
    "five_star_reviews" INTEGER,
    "four_star_reviews" INTEGER,
    "three_star_reviews" INTEGER,
    "two_star_reviews" INTEGER,
    "one_star_reviews" INTEGER
);

-- Reviews Table (review_id set to auto-increment)
CREATE TABLE "reviews" (
    "review_id" SERIAL PRIMARY KEY,
    "product_id" INTEGER REFERENCES "products" ("product_id"),
    "stars" INTEGER,
    "review_date" DATE,
    "verified_purchase" BOOLEAN,
    "manufacturer_replied" BOOLEAN,
    "user_id" UUID REFERENCES "users" ("id"),
    "title" TEXT,
    "review" TEXT,
    "total_found_helpful" INTEGER,
    "images" JSONB
);

-- Product Images Table (image_id set to auto-increment)
CREATE TABLE "product_images" (
    "image_id" SERIAL PRIMARY KEY,
    "product_id" INTEGER REFERENCES "products" ("product_id"),
    "image_url" TEXT NOT NULL
);

-- Feature Bullets Table (feature_id set to auto-increment)
CREATE TABLE "feature_bullets" (
    "feature_id" SERIAL PRIMARY KEY,
    "product_id" INTEGER REFERENCES "products" ("product_id"),
    "bullet" TEXT
);


-- Carts table remains unchanged as UUIDs are auto-generated
CREATE TABLE "carts" (
    "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    "created_by" UUID NOT NULL REFERENCES "users" ("id"),
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "archived" BOOLEAN NOT NULL DEFAULT FALSE
);

-- Cart Items (Composite Primary Key: No Auto-Increment Needed)
CREATE TABLE "cart_items" (
    "cart_id" UUID NOT NULL REFERENCES "carts" ("id"),
    "product_id" INTEGER NOT NULL REFERENCES "products" ("product_id"),
    "quantity" INTEGER NOT NULL CHECK (quantity > 0),
    "price" FLOAT(53) NOT NULL,
    PRIMARY KEY ("cart_id", "product_id")
);

-- Orders table remains unchanged as UUIDs are auto-generated
CREATE TABLE "orders" (
    "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    "user_id" UUID NOT NULL REFERENCES "users" ("id"),
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);

-- Order Lines (ID set to auto-increment)
CREATE TABLE "order_lines" (
    "id" SERIAL PRIMARY KEY,
    "order_id" UUID NOT NULL REFERENCES "orders" ("id"),
    "product_id" INTEGER NOT NULL REFERENCES "products" ("product_id"),
    "price" FLOAT(53) NOT NULL,
    "quantity" INTEGER NOT NULL CHECK (quantity > 0)
);

-- Indexes for better performance
CREATE INDEX "idx_products_name" ON "products" USING GIN (to_tsvector('english', name));
CREATE INDEX "idx_products_brand" ON "products" USING GIN (to_tsvector('english', brand));
CREATE INDEX "idx_products_category" ON "products" USING BTREE (product_category);

-- Additional indexes for faster prefix matching
CREATE INDEX "idx_products_name_lower" ON "products" (lower(name) text_pattern_ops);
CREATE INDEX "idx_products_brand_lower" ON "products" (lower(brand) text_pattern_ops);
CREATE INDEX "idx_products_category_lower" ON "products" (lower(product_category) text_pattern_ops);

