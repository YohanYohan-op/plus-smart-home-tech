package ru.yandex.practicum.commerce.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.store.dto.ProductDto;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductCategory;
import ru.yandex.practicum.commerce.api.store.dto.enums.ProductState;
import ru.yandex.practicum.commerce.api.store.dto.enums.QuantityState;
import ru.yandex.practicum.commerce.store.entity.Product;
import ru.yandex.practicum.commerce.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        log.debug("Creating new product: {}", productDto);
        Product product = productMapper.toProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = productMapper.toProduct(getProduct(productId));
        log.debug("Deactivating product: {}", productId);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.debug("Getting products by category: {}", category);
        Page<Product> products = productRepository.findByProductCategory(category, pageable);
        return products.map(productMapper::toProductDto);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        log.debug("Updating product: {}", productId);
        Product product = productMapper.toProduct(getProduct(productId));
        updateProductFields(product, productDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }

    @Transactional
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.debug("Set product id {} new quantity: {}", productId, quantityState);
        Product product = productMapper.toProduct(getProduct(productId));
        product.setQuantityState(quantityState);
        productRepository.save(product);
        return true;
    }

    public ProductDto getProduct(UUID productId) {
        log.debug("Getting product id {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product id {} not found.", productId);
                    return new ProductNotFoundException("Not found product.");
                });
        return productMapper.toProductDto(product);
    }

    @Transactional
    private void updateProductFields(Product product, ProductDto productDto) {
        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getImageSrc() != null) {
            product.setImageSrc(productDto.getImageSrc());
        }
        if (productDto.getQuantityState() != null) {
            product.setQuantityState(productDto.getQuantityState());
        }
        if (productDto.getProductState() != null) {
            product.setProductState(productDto.getProductState());
        }
        if (productDto.getRating() > 0) {
            product.setRating(productDto.getRating());
        }
        if (productDto.getProductCategory() != null) {
            product.setProductCategory(productDto.getProductCategory());
        }
        if (productDto.getPrice() != null && productDto.getPrice().compareTo(BigDecimal.ONE) >= 0) {
            product.setPrice(productDto.getPrice());
        }
    }

}
