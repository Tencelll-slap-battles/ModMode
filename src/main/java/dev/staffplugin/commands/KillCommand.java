package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class KillCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public KillCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "kill")) return true;

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /kill <player/entity> [player2 ...]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;

        for (String selector : args) {
            // Try as player name first
            Player target = Bukkit.getPlayer(selector);
            if (target != null) {
                target.setHealth(0);
                staff.sendMessage(Component.text("Killed player " + target.getName() + ".", NamedTextColor.GREEN));
                continue;
            }

            // Try as entity selector (e.g. @e[type=zombie])
            try {
                List<Entity> entities = Bukkit.selectEntities(staff, selector);
                if (entities.isEmpty()) {
                    staff.sendMessage(Component.text("No entities matched: " + selector, NamedTextColor.RED));
                    continue;
                }
                int count = 0;
                for (Entity e : entities) {
                    e.remove();
                    count++;
                }
                staff.sendMessage(Component.text("Killed " + count + " entity/entities matching " + selector + ".", NamedTextColor.GREEN));
            } catch (Exception e) {
                staff.sendMessage(Component.text("Invalid target: " + selector, NamedTextColor.RED));
            }
        }

        return true;
    }
}
