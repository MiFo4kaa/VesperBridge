package fun.vespera.vesperBridge;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessageManager {

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage;
    private FileConfiguration messagesConfig;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Возвращает компонент с автоматической подстановкой префикса
     */
    public Component get(String path, TagResolver... resolvers) {
        String raw = messagesConfig.getString(path, "<red>Missing string: " + path);
        String prefix = messagesConfig.getString("prefix", "");
        return miniMessage.deserialize(prefix + raw, resolvers);
    }

    /**
     * Возвращает чистый компонент без префикса (для киков, списков или title)
     */
    public Component getRaw(String path, TagResolver... resolvers) {
        String raw = messagesConfig.getString(path, "<red>Missing string: " + path);
        return miniMessage.deserialize(raw, resolvers);
    }
}