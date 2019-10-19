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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static skory.productsservice.repository.ProductRepositoryTest.givenProduct;

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
        Product product1 = givenProduct("111-111");
        Product product2 = givenProduct("222-222");
        when(productRepository.findByDeletedIsFalse()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDto> all = productService.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).containsExactly(asDto(product1), asDto(product2));
    }

    @Test
    void whenCreate_thenProductSavedAndReturned() {
        Product savedProduct = new Product(123, "111-222", "aaa", 100, LocalDateTime.now(clock), false);
        when(productRepository.save(any())).thenReturn(savedProduct);

        ProductDto productDto = productService.create(ProductDto.builder().sku("111-222").name("aaa").price(100).build());

        verify(productRepository).save(new Product(0, "111-222", "aaa", 100, LocalDateTime.now(clock), false));
        assertThat(productDto).isEqualTo(asDto(savedProduct));
    }

    @Test
    void givenProductWithGivenSkuAlreadyExists_whenCreate_thenThrowException() {
        when(productRepository.existsBySku("111-111")).thenReturn(true);

        Assertions.assertThrows(DuplicateSkuException.class, () -> productService.create(ProductDto.builder().sku("111-111").build()));
    }

    @Test
    void givenProductDoesNotExist_whenUpdate_returnEmpty() {
        Optional<ProductDto> maybeUpdated = productService.update("111-222", ProductDto.builder().build());

        assertThat(maybeUpdated).isEmpty();
    }

    @Test
    void givenProductExist_whenUpdate_returnUpdatedProduct() {
        when(productRepository.findBySkuAndDeletedIsFalse("111-222")).thenReturn(Optional.of(new Product(123, "111-222", "aaa", 100, LocalDateTime.now(clock), false)));
        Product savedProduct = new Product(123, "111-233", "bbb", 111, LocalDateTime.now(clock), false);
        when(productRepository.save(any())).thenReturn(savedProduct);

        Optional<ProductDto> maybeUpdated = productService.update("111-222", ProductDto.builder().sku("111-233").name("bbb").price(111).build());

        verify(productRepository).save(savedProduct);
        assertThat(maybeUpdated).isPresent();
        assertThat(maybeUpdated.get()).isEqualTo(asDto(savedProduct));
    }

    private ProductDto asDto(Product product) {
        return ProductDto.builder().sku(product.getSku()).name(product.getName()).price(product.getPrice()).created(product.getCreated()).build();
    }
}