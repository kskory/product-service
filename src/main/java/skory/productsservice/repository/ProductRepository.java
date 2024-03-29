package skory.productsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import skory.productsservice.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedIsFalse();

    Optional<Product> findByIdAndDeletedIsFalse(long productId);

    boolean existsBySku(String sku);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.id = :id")
    void markAsDeleted(@Param("id") long id);
}
