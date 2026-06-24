package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public GamemodeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "gamemode")) return true;

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /gamemode <survival|creative|spectator>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        GameMode gm = switch (args[0].toLowerCase()) {
            case "survival", "s", "0" -> GameMode.SURVIVAL;
            case "creative", "c", "1" -> GameMode.CREATIVE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (gm == null) {
            staff.sendMessage(Component.text("Unknown gamemode: " + args[0] + ". Use survival, creative, or spectator.", NamedTextColor.RED));
            return true;
        }

        staff.setGameMode(gm);
        staff.sendMessage(Component.text("Gamemode set to " + gm.name().toLowerCase() + ".", NamedTextColor.GREEN));
        return true;
    }
}
