package com.alten.shop.web;

import com.alten.shop.model.*;
import com.alten.shop.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    public record AddRequest(@NotNull Long productId, @Min(1) int quantity) {}
    public record QuantityRequest(@Min(1) int quantity) {}
    public record CartLine(Long productId, int quantity, Product product) {
        static CartLine of(CartItem i) { return new CartLine(i.getProduct().getId(), i.getQuantity(), i.getProduct()); }
    }

    private final CartService cart;

    public CartController(CartService cart) {
        this.cart = cart;
    }

    @GetMapping
    public List<CartLine> get(Principal p) {
        return cart.findByUserEmail(p.getName()).stream().map(CartLine::of).toList();
    }

    /** Adds quantity to an existing line, or creates it. */
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public CartLine add(@Valid @RequestBody AddRequest r, Principal p) {
        return CartLine.of(cart.add(p.getName(), r.productId(), r.quantity()));
    }

    @PatchMapping("/{productId}")
    public CartLine setQuantity(@PathVariable Long productId, @Valid @RequestBody QuantityRequest r, Principal p) {
        return CartLine.of(cart.setQuantity(p.getName(), productId, r.quantity()));
    }

    @DeleteMapping("/{productId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long productId, Principal p) {
        cart.remove(p.getName(), productId);
    }

    @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(Principal p) {
        cart.clear(p.getName());
    }
}
