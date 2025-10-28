package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cart_product",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shopping_cart_id", "product_id"}))
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private UUID cartId;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartProduct cartItem)) return false;
        return Objects.equals(cartId, cartItem.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cartId);
    }

    @Override
    public String toString() {
        return "CartProduct{" +
                "cartId=" + cartId +
                ", shoppingCart=" + shoppingCart +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
