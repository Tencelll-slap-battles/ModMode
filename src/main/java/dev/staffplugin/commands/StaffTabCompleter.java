package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffTabCompleter implements TabCompleter {

    private final StaffPlugin plugin;

    // Selectors that work for multi-target commands
    private static final List<String> MULTI_SELECTORS = List.of("@a", "@r", "@o", "@p", "@s", "@e");
    // Selectors for single-target commands only
    private static final List<String> SINGLE_SELECTORS = List.of("@p", "@r", "@s");
    // All staff-grantable commands including sudo
    private static final List<String> ALL_GRANTABLE = Stream.concat(
            StaffManager.ALL_COMMANDS.stream(), Stream.of("sudo")
    ).collect(Collectors.toList());

    public StaffTabCompleter(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    private List<String> players() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private List<String> filter(List<String> list, String partial) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> playersAndSelectors(String partial, boolean multiOk) {
        List<String> combined = new ArrayList<>(players());
        combined.addAll(multiOk ? MULTI_SELECTORS : SINGLE_SELECTORS);
        return filter(combined, partial);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        return switch (cmd) {
            case "staff" -> {
                if (args.length == 1) {
                    List<String> opts = new ArrayList<>(List.of("add", "remove"));
                    opts.addAll(players());
                    yield filter(opts, args[0]);
                }
                if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")))
                    yield filter(players(), args[1]);
                if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")))
                    yield filter(ALL_GRANTABLE, args[2]);
                yield List.of();
            }
            case "unstaff" -> args.length == 1 ? filter(players(), args[0]) : List.of();

            // Single-target only
            case "to", "invsee" -> args.length == 1 ? playersAndSelectors(args[0], false) : List.of();

            // Multi-target
            case "bring", "freeze", "title", "kick", "sudo" ->
                    args.length == 1 ? playersAndSelectors(args[0], true) : List.of();

            // ban: player name only (no selectors - banning a selector is dangerous)
            case "ban" -> args.length == 1 ? filter(players(), args[0]) : List.of();

            // unfreeze: only frozen players
            case "unfreeze" -> {
                if (args.length == 1) {
                    List<String> frozen = Bukkit.getOnlinePlayers().stream()
                            .filter(p -> plugin.getStaffManager().isFrozen(p.getUniqueId()))
                            .map(Player::getName)
                            .collect(Collectors.toList());
                    frozen.addAll(MULTI_SELECTORS);
                    yield filter(frozen, args[0]);
                }
                yield List.of();
            }

            // give: player/selector, then item, then amount
            case "give" -> {
                if (args.length == 1) yield playersAndSelectors(args[0], true);
                if (args.length == 2) {
                    String partial = args[1].toUpperCase();
                    yield Arrays.stream(Material.values())
                            .filter(m -> m.isItem() && m.name().startsWith(partial))
                            .map(m -> m.name().toLowerCase())
                            .limit(30)
                            .collect(Collectors.toList());
                }
                if (args.length == 3) yield filter(List.of("1","16","32","64"), args[2]);
                yield List.of();
            }

            case "gamemode" -> args.length == 1
                    ? filter(List.of("survival", "creative", "spectator"), args[0])
                    : List.of();

            default -> List.of();
        };
    }
}
