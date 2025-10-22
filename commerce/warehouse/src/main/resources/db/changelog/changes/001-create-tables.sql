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