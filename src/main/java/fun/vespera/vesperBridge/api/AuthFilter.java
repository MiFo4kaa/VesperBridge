package fun.vespera.vesperBridge.api;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AuthFilter extends Filter {

    private final String expectedToken;

    public AuthFilter(String expectedToken) {
        this.expectedToken = expectedToken;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(exchange, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (!token.equals(expectedToken)) {
            sendUnauthorized(exchange, "Forbidden: Invalid token");
            return;
        }

        chain.doFilter(exchange);
    }

    private void sendUnauthorized(HttpExchange exchange, String message) throws IOException {
        byte[] response = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(401, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    @Override
    public String description() {
        return "Bearer Token Authentication Filter";
    }
}