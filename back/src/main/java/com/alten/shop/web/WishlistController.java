package com.alten.shop.web;

import com.alten.shop.model.Product;
import com.alten.shop.service.WishlistService;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlist;

    public WishlistController(WishlistService wishlist) {
        this.wishlist = wishlist;
    }

    @GetMapping
    public List<Product> get(Principal p) {
        return wishlist.findByUserEmail(p.getName());
    }

    @PostMapping("/{productId}") @ResponseStatus(HttpStatus.CREATED)
    public Product add(@PathVariable Long productId, Principal p) {
        return wishlist.add(p.getName(), productId);
    }

    @DeleteMapping("/{productId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long productId, Principal p) {
        wishlist.remove(p.getName(), productId);
    }
}
