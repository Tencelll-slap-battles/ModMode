package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ToCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public ToCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "to")) return true;
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /to <player|@p|@r|@s>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String selector = args[0];

        // /to only makes sense for single targets - block multi-selectors
        if (SelectorUtil.isMultiSelector(selector)) {
            staff.sendMessage(Component.text("Use a single-target selector for /to (e.g. @p, @r, @s or a player name).", NamedTextColor.RED));
            return true;
        }

        List<Player> targets = SelectorUtil.resolvePlayers(selector, staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(selector), NamedTextColor.RED));
            return true;
        }

        Player target = targets.get(0);
        if (target.equals(staff)) {
            staff.sendMessage(Component.text("You can't teleport to yourself.", NamedTextColor.RED));
            return true;
        }

        staff.teleport(target.getLocation());
        staff.sendMessage(Component.text("Teleported to " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }
}
