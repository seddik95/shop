package com.alten.shop;

import com.alten.shop.model.Product;
import com.alten.shop.repo.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProductRepository repo;
    private final ObjectMapper mapper;

    public DataSeeder(ProductRepository repo, ObjectMapper mapper) { this.repo = repo; this.mapper = mapper; }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() > 0) return;
        try (InputStream in = new ClassPathResource("products.json").getInputStream()) {
            List<Product> list = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, Product.class));
            for (Product p : list) {
                p.setId(null);
                if (p.getQuantity() == null || p.getQuantity() == 0) {
                    p.setQuantity(switch (p.getInventoryStatus()) {
                        case INSTOCK -> 50; case LOWSTOCK -> 5; default -> 0; });
                }
            }
            repo.saveAll(list);
        }
    }
}
