package skory.productsservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.exception.DuplicateSkuException;
import skory.productsservice.model.Product;
import skory.productsservice.repository.ProductRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private Clock clock;

    private ProductRepository productRepository;

    public ProductService(Clock clock, ProductRepository productRepository) {
        this.clock = clock;
        this.productRepository = productRepository;
    }

    public List<ProductDto> findAll() {
        return productRepository.findByDeletedIsFalse()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto create(ProductDto productDto) {
        validateSkuUnique(productDto.getSku());

        Product product = fromDto(productDto);
        product.setCreated(LocalDateTime.now(clock));
        return toDto(productRepository.save(product));
    }

    private void validateSkuUnique(String sku) {
        if (productRepository.existsBySku(sku)) {
            throw new DuplicateSkuException("product with given SKU already exists");
        }
    }

    public Optional<ProductDto> findOne(long productId) {
        return productRepository.findByIdAndDeletedIsFalse(productId)
                .map(this::toDto);
    }

    public void delete(long productId) {
        productRepository.markAsDeleted(productId);
    }

    public Optional<ProductDto> update(long productId, ProductDto productDto) {
        Optional<Product> product = productRepository.findByIdAndDeletedIsFalse(productId);
        product.map(Product::getSku)
                .filter(existingSku -> !existingSku.equals(productDto.getSku()))
                .ifPresent(p -> validateSkuUnique(productDto.getSku()));

        return product
                .map(productEntity -> updateValues(productEntity, productDto))
                .map(productRepository::save)
                .map(this::toDto);
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .created(product.getCreated())
                .build();
    }

    private Product fromDto(ProductDto productDto) {
        return Product.builder()
                .sku(productDto.getSku())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();
    }

    private Product updateValues(Product product, ProductDto productDto) {
        product.setSku(productDto.getSku());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        return product;
    }
}
