package ru.yandex.practicum.dto.in;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.enums.ProductCategory;

public record GetProductsParams(
        ProductCategory productCategory,
        Pageable pageable
) {
}
