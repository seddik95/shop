package com.alten.shop.service;

import com.alten.shop.model.AppUser;
import com.alten.shop.model.CartItem;
import com.alten.shop.model.Product;
import com.alten.shop.repo.CartItemRepository;
import com.alten.shop.repo.ProductRepository;
import com.alten.shop.repo.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private final CartItemRepository items;
    private final ProductRepository products;
    private final UserRepository users;

    public CartService(CartItemRepository items, ProductRepository products, UserRepository users) {
        this.items = items;
        this.products = products;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByUserEmail(String email) {
        return items.findByUser(findUser(email));
    }

    @Transactional
    public CartItem add(String email, Long productId, int quantity) {
        AppUser user = findUser(email);
        Product product = products.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown product"));
        CartItem item = items.findByUserAndProductId(user, product.getId())
                .orElseGet(() -> new CartItem(user, product, 0));
        item.setQuantity(item.getQuantity() + quantity);
        return items.save(item);
    }

    @Transactional
    public CartItem setQuantity(String email, Long productId, int quantity) {
        CartItem item = items.findByUserAndProductId(findUser(email), productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        item.setQuantity(quantity);
        return items.save(item);
    }

    @Transactional
    public void remove(String email, Long productId) {
        items.findByUserAndProductId(findUser(email), productId).ifPresent(items::delete);
    }

    @Transactional
    public void clear(String email) {
        items.deleteAll(items.findByUser(findUser(email)));
    }

    private AppUser findUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
