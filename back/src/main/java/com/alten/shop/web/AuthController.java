package com.alten.shop.web;

import com.alten.shop.model.AppUser;
import com.alten.shop.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    public record AccountRequest(@NotBlank String username, @NotBlank String firstname,
                                 @NotBlank @Email String email, @NotBlank @Size(min = 6) String password) {}
    public record TokenRequest(@NotBlank String email, @NotBlank String password) {}

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/account")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody AccountRequest r) {
        AppUser user = auth.createAccount(r.username(), r.firstname(), r.email(), r.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", user.getId(), "username", user.getUsername(),
                             "firstname", user.getFirstname(), "email", user.getEmail()));
    }

    @PostMapping("/token")
    public Map<String, String> token(@Valid @RequestBody TokenRequest r) {
        return Map.of("token", auth.createToken(r.email(), r.password()));
    }
}
