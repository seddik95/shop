package com.alten.shop.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;

    public CartItem() {}
    public CartItem(AppUser user, Product product, int quantity) {
        this.user = user; this.product = product; this.quantity = quantity;
    }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
