package com.alten.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Uses a real servlet container (RANDOM_PORT) because MockMvc never performs the container's
 * ERROR dispatch, which is where a validation error (400) used to be turned into a 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ErrorStatusTest {
    @LocalServerPort int port;
    @Autowired ObjectMapper om;
    final HttpClient http = HttpClient.newHttpClient();

    int call(String method, String path, String body, String token) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) b.header("Authorization", "Bearer " + token);
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString()).statusCode();
    }

    String token(String email) throws Exception {
        call("POST", "/account", "{\"username\":\"u\",\"firstname\":\"f\",\"email\":\"" + email + "\",\"password\":\"secret1\"}", null);
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\",\"password\":\"secret1\"}")).build(),
                HttpResponse.BodyHandlers.ofString());
        return om.readTree(r.body()).get("token").asText();
    }

    @Test
    void adminErrorsKeepTheirRealStatus() throws Exception {
        String admin = token("admin@admin.com");
        String user = token("plain@x.com");
        System.out.println("RESULT valid product, admin        = " + call("POST", "/products", "{\"code\":\"c1\",\"name\":\"X\",\"price\":10}", admin));
        System.out.println("RESULT blank code, admin           = " + call("POST", "/products", "{\"code\":\"\",\"name\":\"X\",\"price\":10}", admin));
        System.out.println("RESULT malformed JSON, admin       = " + call("POST", "/products", "{bad", admin));
        System.out.println("RESULT unknown product PATCH admin = " + call("PATCH", "/products/999999", "{\"code\":\"c\",\"name\":\"X\",\"price\":1}", admin));
        System.out.println("RESULT valid product, plain user   = " + call("POST", "/products", "{\"code\":\"c1\",\"name\":\"X\",\"price\":10}", user));
        System.out.println("RESULT garbage token               = " + call("POST", "/products", "{\"code\":\"c1\",\"name\":\"X\",\"price\":10}", "abc.def.ghi"));
        assertEquals(400, call("POST", "/products", "{\"code\":\"\",\"name\":\"X\",\"price\":10}", admin));
        assertEquals(400, call("POST", "/products", "{bad", admin));
        assertEquals(404, call("PATCH", "/products/999999", "{\"code\":\"c\",\"name\":\"X\",\"price\":1}", admin));
        assertEquals(403, call("POST", "/products", "{\"code\":\"c1\",\"name\":\"X\",\"price\":10}", user));
        assertEquals(401, call("POST", "/products", "{\"code\":\"c1\",\"name\":\"X\",\"price\":10}", "abc.def.ghi"));
    }
}
