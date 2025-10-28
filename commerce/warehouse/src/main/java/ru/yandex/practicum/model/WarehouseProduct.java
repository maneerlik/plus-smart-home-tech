package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "products")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;
    private boolean fragile;

    @Embedded
    private Dimension dimension;
    private double weight;
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarehouseProduct that)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}
