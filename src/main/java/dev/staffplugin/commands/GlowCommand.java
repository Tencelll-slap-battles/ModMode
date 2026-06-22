package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class GlowCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public GlowCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "glow")) return true;

        if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.sendMessage(Component.text("Usage: /glow <on|off>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        boolean enable = args[0].equalsIgnoreCase("on");

        applyGlow(staff, enable);

        if (enable) {
            staff.sendMessage(Component.text("Glow enabled. You are now visible as MODMODE.", NamedTextColor.RED));
        } else {
            staff.sendMessage(Component.text("Glow disabled.", NamedTextColor.GREEN));
        }

        return true;
    }

    /**
     * Applies or removes glow + custom name. Called from here and from ModModeCommand on disable.
     */
    public static void applyGlow(Player player, boolean enable) {
        if (enable) {
            player.setGlowing(true);
            // Store original display name in metadata so we can restore it
            player.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey("staffplugin", "original_name"),
                org.bukkit.persistence.PersistentDataType.STRING,
                player.getCustomName() != null ? player.getCustomName() : ""
            );
            player.customName(Component.text("MODMODE", NamedTextColor.RED));
            player.setCustomNameVisible(true);
        } else {
            player.setGlowing(false);
            // Restore original name
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey("staffplugin", "original_name");
            String original = player.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
            if (original != null && !original.isEmpty()) {
                player.customName(Component.text(original));
            } else {
                player.customName(null);
                player.setCustomNameVisible(false);
            }
            player.getPersistentDataContainer().remove(key);
        }
    }
}
