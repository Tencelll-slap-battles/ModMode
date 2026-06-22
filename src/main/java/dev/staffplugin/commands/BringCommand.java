package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BringCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public BringCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "bring")) return true;

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /bring <player> [player2 ...]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;

        for (String name : args) {
            Player target = Bukkit.getPlayer(name);
            if (target == null) {
                staff.sendMessage(Component.text("Player not found: " + name, NamedTextColor.RED));
                continue;
            }
            if (target.equals(staff)) {
                staff.sendMessage(Component.text("You can't bring yourself.", NamedTextColor.RED));
                continue;
            }
            target.teleport(staff.getLocation());
            staff.sendMessage(Component.text("Brought " + target.getName() + " to you.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("You were teleported to " + staff.getName() + ".", NamedTextColor.YELLOW));
        }

        return true;
    }
}
