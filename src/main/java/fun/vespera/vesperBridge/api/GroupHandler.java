package fun.vespera.vesperBridge.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GroupHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        if (!"GET".equals(method)) {
            sendResponse(exchange, 405, createError("Method Not Allowed. Use GET"));
            return;
        }

        if (!"/api/groups/bloom".equals(path)) {
            sendResponse(exchange, 404, createError("Endpoint not found"));
            return;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            // Ищем всех пользователей, у которых есть узел наследования группы "bloom"
            NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder("bloom").build());

            luckPerms.getUserManager().searchAll(matcher).thenAccept(results -> {
                try {
                    List<String> playerNames = new ArrayList<>();

                    for (UUID uuid : results.keySet()) {
                        // Пытаемся получить имя из LuckPerms кеша, либо через Bukkit OfflinePlayer
                        String name = luckPerms.getUserManager().getUser(uuid) != null
                                ? luckPerms.getUserManager().getUser(uuid).getUsername()
                                : null;

                        if (name == null) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                            name = offlinePlayer.getName();
                        }

                        // Если имя не найдено (офлайн-режим/ни разу не заходил в текущей сессии), отдаем UUID
                        playerNames.add(name != null ? name : uuid.toString());
                    }

                    Collections.sort(playerNames);

                    JsonObject response = new JsonObject();
                    response.addProperty("group", "bloom");
                    response.addProperty("count", playerNames.size());

                    JsonArray playersArray = new JsonArray();
                    playerNames.forEach(playersArray::add);
                    response.add("players", playersArray);

                    sendResponse(exchange, 200, response.toString());
                } catch (Exception e) {
                    try {
                        sendResponse(exchange, 500, createError("Failed to process LuckPerms data: " + e.getMessage()));
                    } catch (IOException ignored) {}
                }
            }).exceptionally(throwable -> {
                try {
                    sendResponse(exchange, 500, createError("LuckPerms query failed: " + throwable.getMessage()));
                } catch (IOException ignored) {}
                return null;
            });

        } catch (IllegalStateException e) {
            sendResponse(exchange, 503, createError("LuckPerms API is not available"));
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