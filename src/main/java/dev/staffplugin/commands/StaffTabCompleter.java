package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffTabCompleter implements TabCompleter {

    private final StaffPlugin plugin;

    public StaffTabCompleter(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    private List<String> onlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private List<String> filter(List<String> list, String partial) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();

        return switch (cmd) {
            case "staff" -> {
                if (args.length == 1) yield filter(List.of("add", "remove"), args[0]);
                if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")))
                    yield filter(onlinePlayers(), args[1]);
                if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")))
                    yield filter(StaffManager.ALL_COMMANDS, args[2]);
                if (args.length == 1) yield filter(onlinePlayers(), args[0]);
                yield List.of();
            }
            case "unstaff" -> args.length == 1 ? filter(onlinePlayers(), args[0]) : List.of();
            case "to", "freeze", "kick", "ban", "title" ->
                    args.length == 1 ? filter(onlinePlayers(), args[0]) : List.of();
            case "bring" -> filter(onlinePlayers(), args[args.length - 1]);
            case "unfreeze" -> {
                if (args.length == 1) {
                    List<String> frozen = Bukkit.getOnlinePlayers().stream()
                            .filter(p -> plugin.getStaffManager().isFrozen(p.getUniqueId()))
                            .map(Player::getName)
                            .collect(Collectors.toList());
                    yield filter(frozen, args[0]);
                }
                yield List.of();
            }
            case "gamemode" -> args.length == 1
                    ? filter(List.of("survival", "creative", "spectator"), args[0])
                    : List.of();
            default -> List.of();
        };
    }
}
