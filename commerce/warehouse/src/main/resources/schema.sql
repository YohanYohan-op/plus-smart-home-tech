CREATE USER warehouse_app WITH PASSWORD 'Pa$$word';
CREATE DATABASE warehouse_db;
GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO warehouse_app;

CREATE TABLE IF NOT EXISTS warehouse (
    product_id UUID PRIMARY KEY,
    fragile    BOOLEAN,
    width      DOUBLE PRECISION,
    height     DOUBLE PRECISION,
    depth      DOUBLE PRECISION,
    weight     DOUBLE PRECISION,
    quantity   INTEGER DEFAULT 0
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO warehouse_app;