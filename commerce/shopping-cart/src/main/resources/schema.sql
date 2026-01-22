CREATE USER shopping_cart_app WITH PASSWORD 'Pa$$word';
CREATE DATABASE shopping_cart_db;
GRANT ALL PRIVILEGES ON DATABASE shopping_cart_db TO shopping_cart_app;

CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username         VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS shopping_cart_items (
    shopping_cart_id UUID    NOT NULL,
    product_id       UUID    NOT NULL,
    quantity         INTEGER NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id),
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts (shopping_cart_id) ON DELETE CASCADE
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO shopping_cart_app;