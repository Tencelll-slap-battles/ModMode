package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public FreezeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "freeze")) return true;

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /freeze <player>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (plugin.getStaffManager().isFrozen(target.getUniqueId())) {
            staff.sendMessage(Component.text(target.getName() + " is already frozen.", NamedTextColor.YELLOW));
            return true;
        }

        plugin.getStaffManager().freeze(target.getUniqueId());
        staff.sendMessage(Component.text("Froze " + target.getName() + ".", NamedTextColor.GREEN));
        target.sendMessage(Component.text("You have been frozen by a staff member.", NamedTextColor.RED));
        return true;
    }
}
