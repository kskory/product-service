package skory.productsservice.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import skory.productsservice.model.Product;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void givenProductExists_whenFindBySku_thenReturnProduct() {
        Product saved = entityManager.persistAndFlush(productWith("111-111"));

        Optional<Product> maybeFound = productRepository.findByIdAndDeletedIsFalse(saved.getId());

        assertThat(maybeFound).isPresent();
        maybeFound.ifPresent(found ->
                assertThat(found).isEqualTo(saved)
        );
    }

    @Test
    public void givenProductDoesNotExist_whenFindBySku_thenReturnEmpty() {
        Optional<Product> maybeFound = productRepository.findByIdAndDeletedIsFalse(1L);

        assertThat(maybeFound).isEmpty();
    }

    @Test
    public void givenProductExists_whenExistsBySku_thenReturnTrue() {
        entityManager.persistAndFlush(productWith("111-111"));

        boolean exists = productRepository.existsBySku("111-111");

        assertThat(exists).isTrue();
    }

    @Test
    public void givenProductDoesNotExist_whenExistsBySku_thenReturnFalse() {
        boolean exists = productRepository.existsBySku("111-111");

        assertThat(exists).isFalse();
    }

    @Test
    public void givenProductExists_whenMarkAsDeleted_thenDeletedFlagSet() {
        long productId = entityManager.persistAndFlush(productWith("111-111")).getId();

        productRepository.markAsDeleted(productId);
        entityManager.clear();

        Optional<Product> updated = productRepository.findById(productId);
        assertThat(updated.get().isDeleted()).isTrue();
    }

    @Test
    public void givenProducts_whenFindByDeletedIsFalse_thenReturnOnlyNotDeleted() {
        Arrays.asList(
                productWith("111-111", false),
                productWith("222-222", true),
                productWith("333-333", false),
                productWith("444-444", true)
        ).forEach(entityManager::persistAndFlush);

        List<Product> products = productRepository.findByDeletedIsFalse();

        assertThat(products).hasSize(2);
        assertThat(products).noneMatch(Product::isDeleted);
    }

    @Test
    public void givenNegativePrice_whenSave_thenThrowException() {
        Product product = product().price(-10).build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> productRepository.save(product));
    }

    //more validation tests

    public static Product productWith(String sku) {
        return productWith(sku, false);
    }

    public static Product productWith(String sku, boolean isDeleted) {
        return product()
                .sku(sku)
                .deleted(isDeleted)
                .build();
    }

    public static Product.ProductBuilder product() {
        return Product.builder()
                .name("one")
                .sku("111-222")
                .price(10)
                .created(LocalDateTime.of(2016, 11, 11, 11, 11, 11))
                .deleted(false);
    }
}