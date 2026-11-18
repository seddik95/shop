package com.alten.shop.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class WishlistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "product_id")
    private Product product;

    public WishlistItem() {}
    public WishlistItem(AppUser user, Product product) { this.user = user; this.product = product; }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public Product getProduct() { return product; }
}
