package com.alten.shop.service;

import com.alten.shop.model.Product;
import com.alten.shop.repo.CartItemRepository;
import com.alten.shop.repo.ProductRepository;
import com.alten.shop.repo.WishlistItemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final ProductRepository products;
    private final CartItemRepository carts;
    private final WishlistItemRepository wishlists;

    public ProductService(ProductRepository products, CartItemRepository carts, WishlistItemRepository wishlists) {
        this.products = products;
        this.carts = carts;
        this.wishlists = wishlists;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return products.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Product create(Product product) {
        product.setId(null);
        product.setCreatedAt(null);
        product.setUpdatedAt(null);
        return products.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product existing = findById(id);
        product.setId(id);
        product.setCreatedAt(existing.getCreatedAt());
        return products.save(product);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        carts.deleteByProductId(id);
        wishlists.deleteByProductId(id);
        products.deleteById(id);
    }
}
