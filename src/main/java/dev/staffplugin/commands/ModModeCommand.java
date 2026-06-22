package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ModModeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public ModModeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        StaffManager sm = plugin.getStaffManager();

        if (!sm.isStaff(player.getUniqueId())) {
            player.sendMessage(Component.text("You are not a staff member.", NamedTextColor.RED));
            return true;
        }

        if (!sm.hasCommandPermission(player.getUniqueId(), "modmode")) {
            player.sendMessage(Component.text("You don't have access to /modmode.", NamedTextColor.RED));
            return true;
        }

        boolean nowActive = !sm.isModModeActive(player.getUniqueId());
        sm.setModMode(player.getUniqueId(), nowActive);

        if (nowActive) {
            player.sendMessage(Component.text("Mod mode enabled. Your staff commands are now active.", NamedTextColor.GREEN));
            // Broadcast to ops only
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.isOp() && !op.equals(player)) {
                    op.sendMessage(Component.text(player.getName() + " enabled mod mode.", NamedTextColor.GRAY));
                }
            }
            // Also show to the player themselves if they are op
            if (player.isOp()) {
                // already sent the enable message above
            }
            // Announce to all ops (including self if op)
            broadcastToOps(player.getName() + " enabled mod mode.", player, sm);
        } else {
            // Disable modmode: set to survival
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Component.text("Mod mode disabled. You are now in survival mode.", NamedTextColor.YELLOW));
            broadcastToOps(player.getName() + " disabled mod mode.", player, sm);
        }

        return true;
    }

    private void broadcastToOps(String message, Player toggler, StaffManager sm) {
        Component msg = Component.text(message, NamedTextColor.GRAY);
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.isOp()) {
                op.sendMessage(msg);
            }
        }
        // If the toggler is not opped, they already got their own enable/disable message
        // (We still broadcast to them if they are op, above loop handles that)
    }
}
