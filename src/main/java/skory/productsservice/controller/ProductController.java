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

    @GetMapping("/{sku}")
    public ResponseEntity<ProductDto> findOne(@PathVariable String sku) {
        return ResponseEntity.of(productService.findOne(sku));
    }

    @GetMapping
    public List<ProductDto> findAll() {
        return productService.findAll();
    }

    @PostMapping
    public ProductDto create(@Valid @RequestBody ProductDto product) {
        return productService.create(product);
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductDto> update(@PathVariable String sku, @Valid @RequestBody ProductDto product) {
        return ResponseEntity.of(productService.update(sku, product));
    }

    @DeleteMapping("/{sku}")
    public void delete(@PathVariable String sku) {
        productService.delete(sku);
    }
}
