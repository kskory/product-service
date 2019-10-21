package skory.productsservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.exception.DuplicateSkuException;
import skory.productsservice.model.Product;
import skory.productsservice.repository.ProductRepository;
import skory.productsservice.repository.ProductRepositoryTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static skory.productsservice.repository.ProductRepositoryTest.productWith;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    private ProductService productService;

    @BeforeEach
    public void setup() {
        productService = new ProductService(clock, productRepository);
    }

    @Test
    void givenProducts_whenFindAll_thenReturnProductDtos() {
        Product product1 = productWith("111-111");
        Product product2 = productWith("222-222");
        when(productRepository.findByDeletedIsFalse()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDto> all = productService.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).containsExactly(asDto(product1), asDto(product2));
    }

    @Test
    void whenCreate_thenProductSavedAndReturned() {
        Product savedProduct = ProductRepositoryTest.product()
                .sku("111-222")
                .name("aaa")
                .price(100)
                .created(now(clock))
                .id(123)
                .build();
        when(productRepository.save(any())).thenReturn(savedProduct);

        ProductDto productDto = productService.create(
                ProductDto.builder()
                        .sku("111-222")
                        .name("aaa")
                        .price(100)
                        .build()
        );

        verify(productRepository).save(
                ProductRepositoryTest.product()
                        .sku("111-222")
                        .name("aaa")
                        .price(100)
                        .created(now(clock))
                        .build()
        );
        assertThat(productDto).isEqualTo(asDto(savedProduct));
    }

    @Test
    void givenProductWithGivenSkuAlreadyExists_whenCreate_thenThrowException() {
        when(productRepository.existsBySku("111-111")).thenReturn(true);

        Assertions.assertThrows(DuplicateSkuException.class, () ->
                productService.create(ProductDto.builder().sku("111-111").build())
        );
    }

    @Test
    void givenProductDoesNotExist_whenUpdate_thenReturnEmpty() {
        Optional<ProductDto> maybeUpdated = productService.update(123, ProductDto.builder().build());

        assertThat(maybeUpdated).isEmpty();
    }

    @Test
    void givenProductExist_whenUpdate_thenReturnUpdatedProduct() {
        Product existingProduct = ProductRepositoryTest.product().id(123).name("aaa").price(10).build();
        when(productRepository.findByIdAndDeletedIsFalse(123)).thenReturn(Optional.of(existingProduct));
        Product savedProduct = ProductRepositoryTest.product().id(123).name("bbb").price(111).build();
        when(productRepository.save(any())).thenReturn(savedProduct);

        ProductDto toSave = ProductDto.builder()
                .sku(existingProduct.getSku())
                .name("bbb")
                .price(111)
                .build();
        Optional<ProductDto> maybeUpdated = productService.update(123, toSave);

        verify(productRepository).save(savedProduct);
        assertThat(maybeUpdated).isPresent();
        assertThat(maybeUpdated.get()).isEqualTo(asDto(savedProduct));
    }

    @Test
    void givenSkuAlreadyExists_whenUpdate_thenThrowException() {
        Product existingProduct = ProductRepositoryTest.product().sku("111-222").build();
        when(productRepository.findByIdAndDeletedIsFalse(123)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("111-333")).thenReturn(true);

        Assertions.assertThrows(DuplicateSkuException.class, () ->
                productService.update(123, ProductDto.builder().sku("111-333").build())
        );
    }

    @Test
    void givenSkuDoesNotExist_whenUpdate_thenReturnUpdatedProduct() {
        Product existingProduct = ProductRepositoryTest.product().id(123).sku("111-222").build();
        when(productRepository.findByIdAndDeletedIsFalse(123)).thenReturn(Optional.of(existingProduct));
        Product savedProduct = ProductRepositoryTest.product().id(123).sku("111-333").build();
        when(productRepository.save(any())).thenReturn(savedProduct);

        ProductDto toSave = ProductDto.builder()
                .sku("111-333")
                .price(existingProduct.getPrice())
                .name(existingProduct.getName())
                .build();
        Optional<ProductDto> maybeUpdated = productService.update(123, toSave);

        verify(productRepository).save(savedProduct);
        assertThat(maybeUpdated).isPresent();
        assertThat(maybeUpdated.get()).isEqualTo(asDto(savedProduct));
    }

    private ProductDto asDto(Product product) {
        return ProductDto.builder().id(product.getId()).sku(product.getSku()).name(product.getName()).price(product.getPrice()).created(product.getCreated()).build();
    }
}