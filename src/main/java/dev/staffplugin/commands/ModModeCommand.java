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
            broadcastToOps(player.getName() + " enabled mod mode.");
        } else {
            // Disable modmode: reset gamemode, remove glow
            player.setGameMode(GameMode.SURVIVAL);
            GlowCommand.applyGlow(player, false, this.plugin);
            player.sendMessage(Component.text("Mod mode disabled. You are now in survival mode.", NamedTextColor.YELLOW));
            broadcastToOps(player.getName() + " disabled mod mode.");
        }

        return true;
    }

    private void broadcastToOps(String message) {
        Component msg = Component.text(message, NamedTextColor.GRAY);
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.isOp()) {
                op.sendMessage(msg);
            }
        }
    }
}
