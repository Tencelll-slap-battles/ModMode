package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public KickCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "kick")) return true;

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /kick <player> [message]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))
                : "You have been kicked.";

        target.kick(Component.text(reason, NamedTextColor.RED));
        staff.sendMessage(Component.text("Kicked " + target.getName() + ": " + reason, NamedTextColor.GREEN));
        return true;
    }
}
