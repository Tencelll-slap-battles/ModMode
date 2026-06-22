package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class UnstaffCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public UnstaffCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /unstaff <player>", NamedTextColor.YELLOW));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        StaffManager sm = plugin.getStaffManager();

        if (!sm.isStaff(target.getUniqueId())) {
            sender.sendMessage(Component.text(target.getName() + " is not a staff member.", NamedTextColor.RED));
            return true;
        }

        // Disable modmode first if active
        if (sm.isModModeActive(target.getUniqueId())) {
            sm.setModMode(target.getUniqueId(), false);
            target.setGameMode(GameMode.SURVIVAL);
            // Notify ops
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.isOp()) {
                    op.sendMessage(Component.text(target.getName() + " disabled mod mode.", NamedTextColor.GRAY));
                }
            }
        }

        sm.removeStaff(target.getUniqueId());
        sender.sendMessage(Component.text(target.getName() + " has been removed from the staff team.", NamedTextColor.YELLOW));
        target.sendMessage(Component.text("You have been removed from the staff team.", NamedTextColor.RED));
        return true;
    }
}
