package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.DimensionDto;
import ru.yandex.practicum.dto.in.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;

public final class WarehouseProductMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private WarehouseProductMapper() {

    }

    public static WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest request) {
        WarehouseProduct product = new WarehouseProduct();

        product.setProductId(request.productId());
        product.setFragile(request.fragile());
        product.setDimension(toDimension(request.dimension()));
        product.setWeight(request.weight());

        return product;
    }

    private static Dimension toDimension(DimensionDto dto) {
        Dimension dimension = new Dimension();

        dimension.setDepth(dto.getDepth());
        dimension.setHeight(dto.getHeight());
        dimension.setWidth(dto.getWidth());

        return dimension;
    }
}
