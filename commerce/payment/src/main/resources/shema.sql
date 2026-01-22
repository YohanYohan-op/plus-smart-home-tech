CREATE USER payment_app WITH PASSWORD 'Pa$$word';
CREATE DATABASE payment_db;
GRANT ALL PRIVILEGES ON DATABASE order_db TO order_app;

CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_payment NUMERIC,
    delivery_total NUMERIC,
    fee_total NUMERIC,
    status VARCHAR(15)
);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO payment_app;