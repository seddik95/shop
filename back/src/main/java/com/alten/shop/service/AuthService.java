package com.alten.shop.service;

import com.alten.shop.model.AppUser;
import com.alten.shop.repo.UserRepository;
import com.alten.shop.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AppUser createAccount(String username, String firstname, String email, String password) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(encoder.encode(password));
        return users.save(user);
    }

    @Transactional(readOnly = true)
    public String createToken(String email, String password) {
        AppUser user = users.findByEmailIgnoreCase(email)
                .filter(candidate -> encoder.matches(password, candidate.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return jwt.generate(user.getEmail());
    }
}
