package com.alten.shop.model;

import jakarta.persistence.*;

@Entity
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String v) { this.firstname = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
}
