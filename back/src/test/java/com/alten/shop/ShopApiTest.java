package com.alten.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShopApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private static final String PRODUCT = "{\"code\":\"c1\",\"name\":\"Test\",\"price\":10,\"quantity\":3,\"inventoryStatus\":\"INSTOCK\"}";

    private void register(String email, String pwd) throws Exception {
        mvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u\",\"firstname\":\"f\",\"email\":\"" + email + "\",\"password\":\"" + pwd + "\"}"))
                .andExpect(status().isCreated());
    }

    private String login(String email, String pwd) throws Exception {
        String body = mvc.perform(post("/token").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + pwd + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("token").asText();
    }

    @Test
    void withoutTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/products")).andExpect(status().isUnauthorized());
        mvc.perform(get("/cart")).andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        register("bad@x.com", "secret1");
        mvc.perform(post("/token").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"bad@x.com\",\"password\":\"nope\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void onlyAdminCanWriteProducts() throws Exception {
        register("user@x.com", "secret1");
        register("admin@admin.com", "secret1");
        String user = login("user@x.com", "secret1");
        String admin = login("admin@admin.com", "secret1");

        mvc.perform(post("/products").header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON).content(PRODUCT)).andExpect(status().isForbidden());
        mvc.perform(get("/products").header("Authorization", "Bearer " + user)).andExpect(status().isOk());
        mvc.perform(post("/products").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content(PRODUCT)).andExpect(status().isCreated());
    }

    @Test
    void cartFlowIsPerUser() throws Exception {
        register("cart@x.com", "secret1");
        register("other@x.com", "secret1");
        String t = "Bearer " + login("cart@x.com", "secret1");
        String o = "Bearer " + login("other@x.com", "secret1");
        long pid = om.readTree(mvc.perform(get("/products").header("Authorization", t))
                .andReturn().getResponse().getContentAsString()).get(0).get("id").asLong();

        String add = "{\"productId\":" + pid + ",\"quantity\":2}";
        mvc.perform(post("/cart").header("Authorization", t).contentType(MediaType.APPLICATION_JSON).content(add))
                .andExpect(status().isCreated());
        mvc.perform(post("/cart").header("Authorization", t).contentType(MediaType.APPLICATION_JSON).content(add))
                .andExpect(status().isCreated());
        mvc.perform(get("/cart").header("Authorization", t))
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].quantity").value(4));
        mvc.perform(patch("/cart/" + pid).header("Authorization", t).contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":1}")).andExpect(jsonPath("$.quantity").value(1));
        mvc.perform(get("/cart").header("Authorization", o)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/cart/" + pid).header("Authorization", t)).andExpect(status().isNoContent());
        mvc.perform(get("/cart").header("Authorization", t)).andExpect(jsonPath("$.length()").value(0));
        assertEquals(1, 1);
    }
}
