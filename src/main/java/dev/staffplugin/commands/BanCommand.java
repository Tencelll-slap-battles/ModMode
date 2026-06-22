package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BanCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public BanCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "ban")) return true;

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /ban <player> [message]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String targetName = args[0];
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "You have been banned.";

        // Add to ban list by name
        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, null, staff.getName());

        // If online, kick them
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            target.kick(Component.text("Banned: " + reason, NamedTextColor.RED));
        }

        staff.sendMessage(Component.text("Banned " + targetName + ": " + reason, NamedTextColor.GREEN));
        return true;
    }
}
