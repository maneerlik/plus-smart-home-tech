package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "order_booking")
public class OrderBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(
            name = "order_booking_products",
            schema = "commerce_warehouse",
            joinColumns = @JoinColumn(name = "order_booking_id")
    )
    @MapKeyColumn(name = "product_code")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;
}
