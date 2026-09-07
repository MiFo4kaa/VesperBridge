package fun.vespera.vesperBridge.whitelistUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class WhitelistListener implements Listener {

    private final WhitelistManager whitelistManager;

    public WhitelistListener(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();

        if (!whitelistManager.isWhitelisted(username)) {
            // Форматирование кик-сообщения (Kyori Adventure)
            Component kickMessage = Component.text("Вы не добавлены в белый список сервера!\n", NamedTextColor.RED)
                    .append(Component.text("Подайте заявку на нашем Discord сервере.", NamedTextColor.GRAY));

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMessage);
        }
    }
}