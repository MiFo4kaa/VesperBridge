package fun.vespera.vesperBridge;

import fun.vespera.vesperBridge.api.HttpServerManager;
import fun.vespera.vesperBridge.commands.VwlCommand;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistListener;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VesperBridge extends JavaPlugin {

    private WhitelistManager whitelistManager;
    private MessageManager messageManager;
    private HttpServerManager httpServerManager;

    @Override
    public void onEnable() {
        // Конфигурация
        saveDefaultConfig();

        this.messageManager = new MessageManager(this);
        this.whitelistManager = new WhitelistManager(this);

        // Запуск встроенного веб-сервера
        this.httpServerManager = new HttpServerManager(this);
        this.httpServerManager.start();

        // Регистрация листенера
        getServer().getPluginManager().registerEvents(
                new WhitelistListener(this.whitelistManager, this.messageManager),
                this
        );

        // Регистрация команды
        VwlCommand vwlCommand = new VwlCommand(this.whitelistManager, this.messageManager);
        if (getCommand("vwl") != null) {
            getCommand("vwl").setExecutor(vwlCommand);
            getCommand("vwl").setTabCompleter(vwlCommand);
        }
    }

    @Override
    public void onDisable() {
        if (httpServerManager != null) {
            httpServerManager.stop();
        }
    }

    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}