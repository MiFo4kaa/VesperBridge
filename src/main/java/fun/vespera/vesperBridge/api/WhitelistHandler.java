package fun.vespera.vesperBridge.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class WhitelistHandler implements HttpHandler {

    private final WhitelistManager whitelistManager;
    private final Gson gson = new Gson();

    public WhitelistHandler(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (path) {
                case "/api/whitelist/add" -> handleAdd(exchange, method);
                case "/api/whitelist/remove" -> handleRemove(exchange, method);
                case "/api/whitelist/list" -> handleList(exchange, method);
                default -> sendResponse(exchange, 404, createError("Endpoint not found"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, createError("Internal server error: " + e.getMessage()));
        }
    }

    private void handleAdd(HttpExchange exchange, String method) throws IOException {
        if (!"POST".equals(method)) {
            sendResponse(exchange, 405, createError("Method Not Allowed. Use POST"));
            return;
        }

        JsonObject body = parseJsonBody(exchange);
        if (body == null || !body.has("username") || body.get("username").getAsString().trim().isEmpty()) {
            sendResponse(exchange, 400, createError("Field 'username' is required"));
            return;
        }

        String username = body.get("username").getAsString().trim();
        boolean added = whitelistManager.addPlayer(username);

        JsonObject response = new JsonObject();
        response.addProperty("username", username);
        response.addProperty("success", true);
        response.addProperty("status", added ? "ADDED" : "ALREADY_EXISTS");

        sendResponse(exchange, 200, response.toString());
    }

    private void handleRemove(HttpExchange exchange, String method) throws IOException {
        if (!"POST".equals(method)) {
            sendResponse(exchange, 405, createError("Method Not Allowed. Use POST"));
            return;
        }

        JsonObject body = parseJsonBody(exchange);
        if (body == null || !body.has("username") || body.get("username").getAsString().trim().isEmpty()) {
            sendResponse(exchange, 400, createError("Field 'username' is required"));
            return;
        }

        String username = body.get("username").getAsString().trim();
        boolean removed = whitelistManager.removePlayer(username);

        JsonObject response = new JsonObject();
        response.addProperty("username", username);
        response.addProperty("success", true);
        response.addProperty("status", removed ? "REMOVED" : "NOT_FOUND");

        sendResponse(exchange, 200, response.toString());
    }

    private void handleList(HttpExchange exchange, String method) throws IOException {
        if (!"GET".equals(method)) {
            sendResponse(exchange, 405, createError("Method Not Allowed. Use GET"));
            return;
        }

        Set<String> players = whitelistManager.getWhitelistedPlayers();
        JsonObject response = new JsonObject();
        response.addProperty("count", players.size());
        response.add("players", gson.toJsonTree(players));

        sendResponse(exchange, 200, response.toString());
    }

    private JsonObject parseJsonBody(HttpExchange exchange) {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String createError(String message) {
        JsonObject err = new JsonObject();
        err.addProperty("error", message);
        return err.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}