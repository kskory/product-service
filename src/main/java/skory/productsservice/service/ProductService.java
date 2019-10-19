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
        if (productRepository.existsBySku(productDto.getSku())) {
            throw new DuplicateSkuException("product with given SKU already exists");
        }

        Product product = fromDto(productDto);
        product.setCreated(LocalDateTime.now(clock));
        return toDto(productRepository.save(product));
    }


    public Optional<ProductDto> findOne(String sku) {
        return productRepository.findBySkuAndDeletedIsFalse(sku)
                .map(this::toDto);
    }

    public void delete(String sku) {
        productRepository.markAsDeleted(sku);
    }

    public Optional<ProductDto> update(String sku, ProductDto productDto) {
        Optional<Product> product = productRepository.findBySkuAndDeletedIsFalse(sku);
        return product
                .map(productEntity -> updateValues(productEntity, productDto))
                .map(productRepository::save)
                .map(this::toDto);
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
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
