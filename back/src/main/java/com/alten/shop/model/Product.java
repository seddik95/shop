package com.alten.shop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class    Product {
    public enum InventoryStatus { INSTOCK, LOWSTOCK, OUTOFSTOCK }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false)
    private String code;
    @NotBlank @Column(nullable = false)
    private String name;
    @Column(length = 2000)
    private String description;
    private String image;
    private String category;
    @NotNull @PositiveOrZero
    private Double price;
    @PositiveOrZero
    private Integer quantity = 0;
    private String internalReference;
    private Long shellId;
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus = InventoryStatus.INSTOCK;
    @Min(0)
    private Double rating;
    private Long createdAt;
    private Long updatedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = System.currentTimeMillis(); }
    @PreUpdate void onUpdate() { updatedAt = System.currentTimeMillis(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getInternalReference() { return internalReference; }
    public void setInternalReference(String r) { this.internalReference = r; }
    public Long getShellId() { return shellId; }
    public void setShellId(Long shellId) { this.shellId = shellId; }
    public InventoryStatus getInventoryStatus() { return inventoryStatus; }
    public void setInventoryStatus(InventoryStatus s) { this.inventoryStatus = s; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}

