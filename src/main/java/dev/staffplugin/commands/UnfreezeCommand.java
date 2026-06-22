package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public UnfreezeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "unfreeze")) return true;

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /unfreeze <player>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (!plugin.getStaffManager().isFrozen(target.getUniqueId())) {
            staff.sendMessage(Component.text(target.getName() + " is not frozen.", NamedTextColor.YELLOW));
            return true;
        }

        plugin.getStaffManager().unfreeze(target.getUniqueId());
        staff.sendMessage(Component.text("Unfroze " + target.getName() + ".", NamedTextColor.GREEN));
        target.sendMessage(Component.text("You have been unfrozen.", NamedTextColor.GREEN));
        return true;
    }
}
