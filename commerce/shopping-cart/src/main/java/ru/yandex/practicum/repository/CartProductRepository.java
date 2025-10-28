package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import ru.yandex.practicum.model.CartProduct;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
    Optional<CartProduct> findByProductId(UUID productId);

    @Modifying
    void removeByProductIdIn(Set<UUID> uuids);
}
