package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelper {

    /**
     * Checks whether a player can use a staff command right now.
     * commandName should be the base name WITHOUT the "mod" prefix,
     * matching what's stored in StaffManager (e.g. "tp", "kill", "gamemode").
     */
    public static boolean checkStaffCommand(StaffPlugin plugin, CommandSender sender, String commandName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return false;
        }

        StaffManager sm = plugin.getStaffManager();

        if (!sm.isStaff(player.getUniqueId())) {
            player.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return false;
        }

        if (!sm.isModModeActive(player.getUniqueId())) {
            player.sendMessage(Component.text("You must enable mod mode first. Use /modmodmode.", NamedTextColor.RED));
            return false;
        }

        if (!sm.hasCommandPermission(player.getUniqueId(), commandName.toLowerCase())) {
            player.sendMessage(Component.text("You don't have access to /mod" + commandName + ".", NamedTextColor.RED));
            return false;
        }

        return true;
    }
}
