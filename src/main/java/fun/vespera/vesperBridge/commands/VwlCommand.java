package fun.vespera.vesperBridge.commands;

import fun.vespera.vesperBridge.MessageManager;
import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VwlCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager whitelistManager;
    private final MessageManager messageManager;

    public VwlCommand(WhitelistManager whitelistManager, MessageManager messageManager) {
        this.whitelistManager = whitelistManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messageManager.get("usage"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add": {
                if (!sender.hasPermission("vesperbridge.whitelist.add")) {
                    sender.sendMessage(messageManager.get("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(messageManager.get("usage-add"));
                    return true;
                }
                String targetNick = args[1];
                if (whitelistManager.addPlayer(targetNick)) {
                    sender.sendMessage(messageManager.get("player-added", Placeholder.parsed("player", targetNick)));
                } else {
                    sender.sendMessage(messageManager.get("player-already-whitelisted", Placeholder.parsed("player", targetNick)));
                }
                return true;
            }

            case "remove": {
                if (!sender.hasPermission("vesperbridge.whitelist.remove")) {
                    sender.sendMessage(messageManager.get("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(messageManager.get("usage-remove"));
                    return true;
                }
                String targetNick = args[1];
                if (whitelistManager.removePlayer(targetNick)) {
                    sender.sendMessage(messageManager.get("player-removed", Placeholder.parsed("player", targetNick)));
                } else {
                    sender.sendMessage(messageManager.get("player-not-found", Placeholder.parsed("player", targetNick)));
                }
                return true;
            }

            case "reload": {
                if (!sender.hasPermission("vesperbridge.whitelist.reload")) {
                    sender.sendMessage(messageManager.get("no-permission"));
                    return true;
                }
                whitelistManager.reload();
                messageManager.reload();
                sender.sendMessage(messageManager.get("whitelist-reloaded"));
                return true;
            }

            case "list": {
                if (!sender.hasPermission("vesperbridge.whitelist.list")) {
                    sender.sendMessage(messageManager.get("no-permission"));
                    return true;
                }
                Set<String> players = whitelistManager.getWhitelistedPlayers();
                sender.sendMessage(messageManager.get("whitelist-list",
                        Placeholder.parsed("count", String.valueOf(players.size())),
                        Placeholder.parsed("players", String.join(", ", players))
                ));
                return true;
            }

            default:
                sender.sendMessage(messageManager.get("unknown-subcommand"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            if (sender.hasPermission("vesperbridge.whitelist.add")) subCommands.add("add");
            if (sender.hasPermission("vesperbridge.whitelist.remove")) subCommands.add("remove");
            if (sender.hasPermission("vesperbridge.whitelist.reload")) subCommands.add("reload");
            if (sender.hasPermission("vesperbridge.whitelist.list")) subCommands.add("list");

            return subCommands.stream()
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("vesperbridge.whitelist.remove")) {
            return whitelistManager.getWhitelistedPlayers().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}