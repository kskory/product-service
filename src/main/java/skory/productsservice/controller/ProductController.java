package skory.productsservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findOne(@PathVariable long productId) {
        return ResponseEntity.of(productService.findOne(productId));
    }

    @GetMapping
    public List<ProductDto> findAll() {
        return productService.findAll();
    }

    @PostMapping
    public ProductDto create(@Valid @RequestBody ProductDto product) {
        return productService.create(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable long productId, @Valid @RequestBody ProductDto product) {
        return ResponseEntity.of(productService.update(productId, product));
    }

    @DeleteMapping("/{productId}")
    public void delete(@PathVariable long productId) {
        productService.delete(productId);
    }
}
