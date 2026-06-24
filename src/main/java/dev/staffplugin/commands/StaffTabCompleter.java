package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffTabCompleter implements TabCompleter {

    private final StaffPlugin plugin;

    private static final List<String> MULTI_SELECTORS  = List.of("@a","@r","@o","@p","@s","@e");
    private static final List<String> SINGLE_SELECTORS = List.of("@p","@r","@s");

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

    private List<String> playersAndSelectors(String partial, boolean multi) {
        List<String> combined = new ArrayList<>(players());
        combined.addAll(multi ? MULTI_SELECTORS : SINGLE_SELECTORS);
        return filter(combined, partial);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return switch (command.getName().toLowerCase()) {
            case "staff" -> {
                if (args.length == 1) {
                    List<String> opts = new ArrayList<>(List.of("add","remove"));
                    opts.addAll(players());
                    yield filter(opts, args[0]);
                }
                if (args.length == 2) yield filter(players(), args[1]);
                if (args.length == 3) yield filter(ALL_GRANTABLE, args[2]);
                yield List.of();
            }
            case "unstaff"     -> args.length == 1 ? filter(players(), args[0]) : List.of();
            case "modto",
                 "modinvsee"   -> args.length == 1 ? playersAndSelectors(args[0], false) : List.of();
            case "modbring",
                 "modfreeze",
                 "modtitle",
                 "modkick",
                 "modsudo"     -> args.length >= 1 ? playersAndSelectors(args[args.length-1], true) : List.of();
            case "modban"      -> args.length == 1 ? filter(players(), args[0]) : List.of();
            case "modunfreeze" -> {
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
            case "modgive" -> {
                if (args.length == 1) yield playersAndSelectors(args[0], true);
                if (args.length == 2) {
                    String partial = args[1].toUpperCase();
                    yield java.util.Arrays.stream(Material.values())
                            .filter(m -> m.isItem() && m.name().startsWith(partial))
                            .map(m -> m.name().toLowerCase())
                            .limit(30)
                            .collect(Collectors.toList());
                }
                if (args.length == 3) yield filter(List.of("1","16","32","64"), args[2]);
                yield List.of();
            }
            case "modgamemode" -> args.length == 1
                    ? filter(List.of("survival","creative","spectator"), args[0])
                    : List.of();
            case "modglow"     -> args.length == 1
                    ? filter(List.of("on","off"), args[0])
                    : List.of();
            default -> List.of();
        };
    }
}
