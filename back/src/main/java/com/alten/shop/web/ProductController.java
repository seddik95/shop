package com.alten.shop.web;

import com.alten.shop.model.Product;
import com.alten.shop.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping public List<Product> list() { return products.findAll(); }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return products.findById(id);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product p) {
        return products.create(p);
    }

    @PatchMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product p) {
        return products.update(id, p);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        products.delete(id);
    }
}
