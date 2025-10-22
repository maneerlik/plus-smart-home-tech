package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Modifying
    @Query("UPDATE Product p SET p.quantityState = :quantityState WHERE p.productId = :id")
    int setProductQuantityState(@Param("quantityState") QuantityState quantityState, @Param("id") UUID id);

    @Modifying
    @Query("UPDATE Product p SET p.productState = 'DEACTIVATE' WHERE p.productId = :id")
    int deactivateProduct(@Param("id") UUID id);

    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}
