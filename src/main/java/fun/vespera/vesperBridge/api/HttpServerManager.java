package fun.vespera.vesperBridge.api;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fun.vespera.vesperBridge.VesperBridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class HttpServerManager {

    private final VesperBridge plugin;
    private HttpServer server;

    public HttpServerManager(VesperBridge plugin) {
        this.plugin = plugin;
    }

    public void start() {
        String host = plugin.getConfig().getString("api.host", "127.0.0.1");
        int port = plugin.getConfig().getInt("api.port", 8080);
        String token = plugin.getConfig().getString("api.token", "change_me");

        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);

            WhitelistHandler whitelistHandler = new WhitelistHandler(plugin.getWhitelistManager());
            GroupHandler groupHandler = new GroupHandler();
            AuthFilter authFilter = new AuthFilter(token);

            // Эндпоинты вайтлиста
            registerContext("/api/whitelist/add", whitelistHandler, authFilter);
            registerContext("/api/whitelist/remove", whitelistHandler, authFilter);
            registerContext("/api/whitelist/list", whitelistHandler, authFilter);

            // Эндпоинт для групп
            registerContext("/api/groups/bloom", groupHandler, authFilter);

            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();

            plugin.getLogger().info("API сервер запущен на " + host + ":" + port);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Не удалось запустить внутренний HTTP-сервер", e);
        }
    }

    // Приведите метод к универсальному HttpHandler:
    private void registerContext(String path, HttpHandler handler, AuthFilter filter) {
        HttpContext context = server.createContext(path, handler);
        context.getFilters().add(filter);
    }

    public void stop() {
        if (server != null) {
            server.stop(1); // 1 секунда на завершение текущих сетевых обменов
            plugin.getLogger().info("API сервер остановлен.");
        }
    }
}