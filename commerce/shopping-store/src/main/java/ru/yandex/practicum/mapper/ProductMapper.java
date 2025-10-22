package ru.yandex.practicum.mapper;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductPageDto;
import ru.yandex.practicum.model.Product;

import java.util.List;
import java.util.Objects;

public final class ProductMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private ProductMapper() {

    }

    public static ProductDto toProductDto(Product entity) {
        if (Objects.isNull(entity)) return null;

        return ProductDto.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .imageSrc(entity.getImageSrc())
                .quantityState(entity.getQuantityState())
                .productState(entity.getProductState())
                .productCategory(entity.getProductCategory())
                .price(entity.getPrice())
                .build();
    }

    public static Product toProduct(ProductDto dto) {
        if (Objects.isNull(dto)) return null;

        Product entity = new Product();

        entity.setProductId(dto.getProductId());
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setImageSrc(dto.getImageSrc());
        entity.setQuantityState(dto.getQuantityState());
        entity.setProductState(dto.getProductState());
        entity.setProductCategory(dto.getProductCategory());
        entity.setPrice(dto.getPrice());

        return entity;
    }

    public static ProductPageDto toProductPageDto(Page<Product> products) {
        List<ProductDto> content = products.getContent().stream()
                .map(ProductMapper::toProductDto)
                .toList();

        List<ProductPageDto.SortDto> sortList = products.getSort().stream()
                .map(order -> new ProductPageDto.SortDto(
                        order.getProperty(),
                        order.getDirection().name())
                )
                .toList();

        ProductPageDto pageDto = new ProductPageDto();

        pageDto.setContent(content);
        pageDto.setSort(sortList);

        return pageDto;
    }
}
