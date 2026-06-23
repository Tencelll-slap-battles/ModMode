package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class GlowCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public GlowCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "glow")) return true;

        if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.sendMessage(Component.text("Usage: /staffplugin:glow <on|off>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        boolean enable = args[0].equalsIgnoreCase("on");

        applyGlow(staff, enable, plugin);

        if (enable) {
            staff.sendMessage(Component.text("You are now visible as moderating.", NamedTextColor.RED));
        } else {
            staff.sendMessage(Component.text("Moderating indicators removed.", NamedTextColor.GREEN));
        }

        return true;
    }

    /**
     * Applies or removes all moderating visuals:
     *  - Red glow
     *  - [CURRENTLY MODERATING] nametag above head
     *  - Server-wide chat announcement
     *  - 10-second actionbar flash to all players
     */
    public static void applyGlow(Player player, boolean enable, StaffPlugin plugin) {
        if (enable) {
            // 1. Red glow
            player.setGlowing(true);

            // 2. Save original custom name and set [CURRENTLY MODERATING]
            NamespacedKey nameKey = new NamespacedKey("staffplugin", "original_name");
            player.getPersistentDataContainer().set(
                nameKey,
                PersistentDataType.STRING,
                player.getCustomName() != null ? player.getCustomName() : ""
            );
            player.customName(Component.text("[CURRENTLY MODERATING]", NamedTextColor.RED));
            player.setCustomNameVisible(true);

            // 3. Server-wide chat message
            Component chatMsg = Component.text("🔴 " + player.getName() + " is moderating.", NamedTextColor.RED);
            Bukkit.broadcast(chatMsg);

            // 4. Actionbar flash to all players for 10 seconds (every 10 ticks = 0.5s, 20 times)
            Component actionbarMsg = Component.text("🔴 " + player.getName() + " is moderating.", NamedTextColor.RED);
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 20 || !player.isOnline() || !player.isGlowing()) {
                        cancel();
                        return;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendActionBar(actionbarMsg);
                    }
                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 10L); // every 10 ticks = 0.5s, runs 20 times = 10 seconds

        } else {
            // Remove glow
            player.setGlowing(false);

            // Restore original name
            NamespacedKey nameKey = new NamespacedKey("staffplugin", "original_name");
            String original = player.getPersistentDataContainer().get(nameKey, PersistentDataType.STRING);
            if (original != null && !original.isEmpty()) {
                player.customName(Component.text(original));
            } else {
                player.customName(null);
                player.setCustomNameVisible(false);
            }
            player.getPersistentDataContainer().remove(nameKey);
        }
    }
}
