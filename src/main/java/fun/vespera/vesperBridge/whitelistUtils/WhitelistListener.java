package fun.vespera.vesperBridge.whitelistUtils;

import fun.vespera.vesperBridge.MessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class WhitelistListener implements Listener {

    private final WhitelistManager whitelistManager;
    private final MessageManager messageManager;

    public WhitelistListener(WhitelistManager whitelistManager, MessageManager messageManager) {
        this.whitelistManager = whitelistManager;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();

        if (!whitelistManager.isWhitelisted(username)) {
            Component kickReason = messageManager.getRaw("kick-message");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickReason);
        }
    }
}