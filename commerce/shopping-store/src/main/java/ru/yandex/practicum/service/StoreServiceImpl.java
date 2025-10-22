package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductPageDto;
import ru.yandex.practicum.dto.in.GetProductsParams;
import ru.yandex.practicum.dto.in.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;

import static ru.yandex.practicum.mapper.ProductMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Product product = toProduct(productDto);
        product = productRepository.save(product);

        return toProductDto(product);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        checkExistProduct(productDto.getProductId());
        Product product = productRepository.save(toProduct(productDto));

        return toProductDto(product);
    }

    @Transactional
    @Override
    public boolean deactivateProduct(UUID id) {
        checkExistProduct(id);
        return productRepository.deactivateProduct(id) > 0;
    }

    @Transactional
    @Override
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        checkExistProduct(request.productId());
        return productRepository.setProductQuantityState(request.quantityState(), request.productId()) > 0;
    }

    @Override
    public ProductDto getProductById(UUID id) {
        checkExistProduct(id);
        return toProductDto(productRepository.findById(id).get());
    }

    @Override
    public ProductPageDto getProducts(GetProductsParams getProductsParams) {
        Page<Product> products = productRepository
                .findAllByProductCategory(getProductsParams.productCategory(), getProductsParams.pageable());

        return toProductPageDto(products);
    }

    private void checkExistProduct(UUID id) {
        boolean exists = productRepository.existsById(id);

        if (!exists) {
            log.error("Product with id: {} not found", id);
            throw new ProductNotFoundException(String.format("Product with id: %s not found", id));
        }
    }
}
