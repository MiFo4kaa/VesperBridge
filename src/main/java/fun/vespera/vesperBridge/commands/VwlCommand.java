package fun.vespera.vesperBridge.commands;

import fun.vespera.vesperBridge.whitelistUtils.WhitelistManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VwlCommand implements CommandExecutor, TabCompleter {

    private final WhitelistManager whitelistManager;

    public VwlCommand(WhitelistManager whitelistManager) {
        this.whitelistManager = whitelistManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Использование: /vwl <add|remove|reload|list>", NamedTextColor.RED));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add": {
                if (!sender.hasPermission("vesperbridge.whitelist.add")) {
                    sender.sendMessage(Component.text("У вас нет прав на это действие!", NamedTextColor.RED));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Использование: /vwl add <ник>", NamedTextColor.RED));
                    return true;
                }
                String targetNick = args[1];
                if (whitelistManager.addPlayer(targetNick)) {
                    sender.sendMessage(Component.text("Игрок " + targetNick + " успешно добавлен в whitelist!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Игрок " + targetNick + " уже находится в whitelist.", NamedTextColor.YELLOW));
                }
                return true;
            }

            case "remove": {
                if (!sender.hasPermission("vesperbridge.whitelist.remove")) {
                    sender.sendMessage(Component.text("У вас нет прав на это действие!", NamedTextColor.RED));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Использование: /vwl remove <ник>", NamedTextColor.RED));
                    return true;
                }
                String targetNick = args[1];
                if (whitelistManager.removePlayer(targetNick)) {
                    sender.sendMessage(Component.text("Игрок " + targetNick + " удален из whitelist!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Игрок " + targetNick + " не найден в whitelist.", NamedTextColor.YELLOW));
                }
                return true;
            }

            case "reload": {
                if (!sender.hasPermission("vesperbridge.whitelist.reload")) {
                    sender.sendMessage(Component.text("У вас нет прав на это действие!", NamedTextColor.RED));
                    return true;
                }
                whitelistManager.reload();
                sender.sendMessage(Component.text("Список whitelist успешно перезагружен из файла.", NamedTextColor.GREEN));
                return true;
            }

            case "list": {
                if (!sender.hasPermission("vesperbridge.whitelist.list")) {
                    sender.sendMessage(Component.text("У вас нет прав на это действие!", NamedTextColor.RED));
                    return true;
                }
                var players = whitelistManager.getWhitelistedPlayers();
                sender.sendMessage(Component.text("Игроков в whitelist (" + players.size() + "): ", NamedTextColor.AQUA)
                        .append(Component.text(String.join(", ", players), NamedTextColor.WHITE)));
                return true;
            }

            default:
                sender.sendMessage(Component.text("Неизвестное поддействие. Доступно: add, remove, reload, list", NamedTextColor.RED));
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