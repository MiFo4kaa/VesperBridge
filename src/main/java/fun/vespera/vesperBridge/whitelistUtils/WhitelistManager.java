package fun.vespera.vesperBridge.whitelistUtils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;

public class WhitelistManager {

    private final JavaPlugin plugin;
    private final File whitelistFile;
    // Регистронезависимое потокобезопасное хранилище
    private final Set<String> whitelistedPlayers = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Object fileLock = new Object();

    public WhitelistManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.whitelistFile = new File(plugin.getDataFolder(), "whitelist.yml");
        load();
    }

    /**
     * Загрузка или перезагрузка списка из whitelist.yml
     */
    public void reload() {
        load();
    }

    private void load() {
        synchronized (fileLock) {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (!whitelistFile.exists()) {
                try {
                    whitelistFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Не удалось создать whitelist.yml", e);
                }
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(whitelistFile);
            whitelistedPlayers.clear();
            whitelistedPlayers.addAll(config.getStringList("players"));
        }
    }

    /**
     * Сохранение текущего списка игроков в файл (асинхронно или синхронно)
     */
    private void save() {
        synchronized (fileLock) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("players", new ArrayList<>(whitelistedPlayers));
            try {
                config.save(whitelistFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Не удалось сохранить whitelist.yml", e);
            }
        }
    }

    /**
     * Проверка, есть ли игрок в вайтлисте
     */
    public boolean isWhitelisted(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return whitelistedPlayers.contains(username.trim());
    }

    /**
     * Добавление игрока (готовый метод под вызов из API / команд)
     * @return true, если игрок был успешно добавлен; false, если уже был в списке
     */
    public boolean addPlayer(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        boolean added = whitelistedPlayers.add(username.trim());
        if (added) {
            save();
        }
        return added;
    }

    /**
     * Удаление игрока (готовый метод под вызов из API / команд)
     * @return true, если игрок был найден и удален; false, если его не было
     */
    public boolean removePlayer(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        boolean removed = whitelistedPlayers.remove(username.trim());
        if (removed) {
            save();
        }
        return removed;
    }

    /**
     * Получить неизменяемый набор всех добавленных ников
     */
    public Set<String> getWhitelistedPlayers() {
        return Collections.unmodifiableSet(whitelistedPlayers);
    }
}