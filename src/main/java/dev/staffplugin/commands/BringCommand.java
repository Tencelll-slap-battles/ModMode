package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class BringCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public BringCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "bring")) return true;
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /bring <player|@a|@r|@o|@p|@s> [...]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;

        for (String selector : args) {
            List<Player> targets = SelectorUtil.resolvePlayers(selector, staff);
            if (targets.isEmpty()) {
                staff.sendMessage(Component.text(SelectorUtil.notFound(selector), NamedTextColor.RED));
                continue;
            }
            for (Player target : targets) {
                if (target.equals(staff)) continue;
                target.teleport(staff.getLocation());
                target.sendMessage(Component.text("You were teleported to " + staff.getName() + ".", NamedTextColor.YELLOW));
            }
            staff.sendMessage(Component.text("Brought " + targets.size() + " player(s) to you.", NamedTextColor.GREEN));
        }
        return true;
    }
}
