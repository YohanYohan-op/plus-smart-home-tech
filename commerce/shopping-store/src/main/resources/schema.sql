CREATE USER shopping_store_app WITH PASSWORD 'Pa$$word';
CREATE DATABASE shopping_store_db;
GRANT ALL PRIVILEGES ON DATABASE shopping_store_db TO shopping_store_app;

CREATE TABLE IF NOT EXISTS products (
    product_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name     VARCHAR(255)     NOT NULL,
    description      TEXT             NOT NULL,
    image_src        VARCHAR(255),
    quantity_state   VARCHAR(50)      NOT NULL,
    product_state    VARCHAR(50)      NOT NULL,
    rating           DOUBLE PRECISION NOT NULL,
    product_category VARCHAR(50)      NOT NULL,
    price            NUMERIC(19, 2)   NOT NULL
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO shopping_store_app;