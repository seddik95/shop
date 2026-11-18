package com.alten.shop.service;

import com.alten.shop.model.AppUser;
import com.alten.shop.model.Product;
import com.alten.shop.model.WishlistItem;
import com.alten.shop.repo.ProductRepository;
import com.alten.shop.repo.UserRepository;
import com.alten.shop.repo.WishlistItemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WishlistService {
    private final WishlistItemRepository items;
    private final ProductRepository products;
    private final UserRepository users;

    public WishlistService(WishlistItemRepository items, ProductRepository products, UserRepository users) {
        this.items = items;
        this.products = products;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Product> findByUserEmail(String email) {
        return items.findByUser(findUser(email)).stream().map(WishlistItem::getProduct).toList();
    }

    @Transactional
    public Product add(String email, Long productId) {
        AppUser user = findUser(email);
        Product product = products.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown product"));
        if (items.findByUserAndProductId(user, productId).isEmpty()) {
            items.save(new WishlistItem(user, product));
        }
        return product;
    }

    @Transactional
    public void remove(String email, Long productId) {
        items.findByUserAndProductId(findUser(email), productId).ifPresent(items::delete);
    }

    private AppUser findUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
