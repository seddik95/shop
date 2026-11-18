package com.alten.shop.repo;

import com.alten.shop.model.AppUser;
import com.alten.shop.model.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(AppUser user);
    Optional<CartItem> findByUserAndProductId(AppUser user, Long productId);
    void deleteByProductId(Long productId);
}
