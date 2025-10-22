-- liquibase formatted sql

-- changeset smirnovs:001-create-tables
CREATE TABLE IF NOT EXISTS shopping_cart
(
    shopping_cart_id UUID PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE cart_product
(
    cart_id          UUID PRIMARY KEY,
    shopping_cart_id UUID    NOT NULL REFERENCES shopping_cart (shopping_cart_id) ON DELETE CASCADE,
    product_id       UUID    NOT NULL,
    quantity         INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (shopping_cart_id, product_id)
);