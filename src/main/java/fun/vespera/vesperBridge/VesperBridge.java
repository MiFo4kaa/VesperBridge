package fun.vespera.vesperBridge;

import fun.vespera.vesperBridge.commands.VwlCommand;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistListener;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VesperBridge extends JavaPlugin {

    private WhitelistManager whitelistManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        this.messageManager = new MessageManager(this);
        this.whitelistManager = new WhitelistManager(this);

        getServer().getPluginManager().registerEvents(
                new WhitelistListener(this.whitelistManager, this.messageManager),
                this
        );

        VwlCommand vwlCommand = new VwlCommand(this.whitelistManager, this.messageManager);
        if (getCommand("vwl") != null) {
            getCommand("vwl").setExecutor(vwlCommand);
            getCommand("vwl").setTabCompleter(vwlCommand);
        }
    }

    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}