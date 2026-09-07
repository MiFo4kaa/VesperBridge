package fun.vespera.vesperBridge;

import fun.vespera.vesperBridge.commands.VwlCommand;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistListener;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VesperBridge extends JavaPlugin {

    private WhitelistManager whitelistManager;

    @Override
    public void onEnable() {
        // Инициализация менеджера
        this.whitelistManager = new WhitelistManager(this);

        // Регистрация листенера
        getServer().getPluginManager().registerEvents(new WhitelistListener(this.whitelistManager), this);

        // Регистрация команды
        VwlCommand vwlCommand = new VwlCommand(this.whitelistManager);
        if (getCommand("vwl") != null) {
            getCommand("vwl").setExecutor(vwlCommand);
            getCommand("vwl").setTabCompleter(vwlCommand);
        }
    }

    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
}