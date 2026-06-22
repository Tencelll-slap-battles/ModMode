package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ToCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public ToCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "to")) return true;

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /to <player>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (target.equals(staff)) {
            staff.sendMessage(Component.text("You can't teleport to yourself.", NamedTextColor.RED));
            return true;
        }

        staff.teleport(target.getLocation());
        staff.sendMessage(Component.text("Teleported to " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }
}
