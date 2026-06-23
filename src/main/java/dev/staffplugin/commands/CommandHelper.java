package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelper {

    /**
     * Returns true if the sender may use the given staff command right now.
     * Sends the appropriate error message if not.
     */
    public static boolean checkStaffCommand(StaffPlugin plugin, CommandSender sender, String command) {
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
            player.sendMessage(Component.text("You must enable mod mode first. Use /modmode.", NamedTextColor.RED));
            return false;
        }

        if (!sm.hasCommandPermission(player.getUniqueId(), command)) {
            player.sendMessage(Component.text("You don't have access to the /" + command + " command.", NamedTextColor.RED));
            return false;
        }

        return true;
    }
}
