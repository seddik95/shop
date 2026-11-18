package com.alten.shop.repo;

import com.alten.shop.model.AppUser;
import com.alten.shop.model.WishlistItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(AppUser user);
    Optional<WishlistItem> findByUserAndProductId(AppUser user, Long productId);
    void deleteByProductId(Long productId);
}
