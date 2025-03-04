#!/bin/bash
set -e

echo "✅ Running SQL import..."

psql -U postgres -d e_commerce <<EOF
-- Load schema and data
\i /sql/init.sql;
\i /sql/users.sql;
\i /sql/products.sql;
\i /sql/product_images.sql;
\i /sql/feature_bullets.sql;
\i /sql/reviews.sql;
\i /sql/carts.sql;
\i /sql/cart_items.sql;
\i /sql/orders.sql;
\i /sql/order_lines.sql;
\i /sql/final_setup.sql;
EOF

echo "🎉 Data import completed!"