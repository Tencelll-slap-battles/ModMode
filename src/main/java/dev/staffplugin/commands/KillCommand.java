package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
            sender.sendMessage(Component.text("Usage: /kill <player|selector> [...]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;

        for (String selector : args) {
            List<Entity> entities = SelectorUtil.resolveEntities(selector, staff);
            if (entities.isEmpty()) {
                staff.sendMessage(Component.text("No entities matched: " + selector, NamedTextColor.RED));
                continue;
            }
            int count = 0;
            for (Entity e : entities) {
                if (e instanceof Player p) {
                    p.setHealth(0);
                } else if (e instanceof LivingEntity le) {
                    le.setHealth(0);
                } else {
                    e.remove();
                }
                count++;
            }
            staff.sendMessage(Component.text("Killed " + count + " entity/entities.", NamedTextColor.GREEN));
        }
        return true;
    }
}
