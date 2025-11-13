-- liquibase formatted sql

-- changeset smirnovs:001-create-tables
CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID PRIMARY KEY,
    fragile          BOOLEAN          NOT NULL,
    quantity         INT              NOT NULL,
    weight           DOUBLE PRECISION NOT NULL,
    dimension_width  DOUBLE PRECISION NOT NULL,
    dimension_height DOUBLE PRECISION NOT NULL,
    dimension_depth  DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS order_booking
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID NOT NULL,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS order_booking_products
(
    order_booking_id UUID NOT NULL,
    product_id       UUID NOT NULL,
    quantity         INT  NOT NULL,
    PRIMARY KEY (order_booking_id, product_id),
    CONSTRAINT fk_order_booking_products FOREIGN KEY (order_booking_id) REFERENCES order_booking (id) ON DELETE CASCADE
);