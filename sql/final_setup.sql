-- Create sequence for products.product_id if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'products_product_id_seq') THEN
        CREATE SEQUENCE products_product_id_seq OWNED BY products.product_id;
        ALTER TABLE products ALTER COLUMN product_id SET DEFAULT nextval('products_product_id_seq');
    END IF;
END $$;

SELECT setval('products_product_id_seq', COALESCE((SELECT MAX(product_id) FROM products), 0) + 1);

-- Create sequence for product_images.image_id if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'product_images_image_id_seq') THEN
        CREATE SEQUENCE product_images_image_id_seq OWNED BY product_images.image_id;
        ALTER TABLE product_images ALTER COLUMN image_id SET DEFAULT nextval('product_images_image_id_seq');
    END IF;
END $$;

SELECT setval('product_images_image_id_seq', COALESCE((SELECT MAX(image_id) FROM product_images), 0) + 1);

-- Create sequence for feature_bullets.feature_id if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'feature_bullets_feature_id_seq') THEN
        CREATE SEQUENCE feature_bullets_feature_id_seq OWNED BY feature_bullets.feature_id;
        ALTER TABLE feature_bullets ALTER COLUMN feature_id SET DEFAULT nextval('feature_bullets_feature_id_seq');
    END IF;
END $$;

SELECT setval('feature_bullets_feature_id_seq', COALESCE((SELECT MAX(feature_id) FROM feature_bullets), 0) + 1);

-- Create sequence for reviews.review_id if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'reviews_review_id_seq') THEN
        CREATE SEQUENCE reviews_review_id_seq OWNED BY reviews.review_id;
        ALTER TABLE reviews ALTER COLUMN review_id SET DEFAULT nextval('reviews_review_id_seq');
    END IF;
END $$;

SELECT setval('reviews_review_id_seq', COALESCE((SELECT MAX(review_id) FROM reviews), 0) + 1);

