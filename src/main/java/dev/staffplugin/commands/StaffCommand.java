package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public StaffCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
            return true;
        }

        StaffManager sm = plugin.getStaffManager();

        // /staff <player>
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
                return true;
            }
            if (sm.isStaff(target.getUniqueId())) {
                sender.sendMessage(Component.text(target.getName() + " is already a staff member.", NamedTextColor.YELLOW));
                return true;
            }
            sm.addStaff(target.getUniqueId());
            sender.sendMessage(Component.text(target.getName() + " has been added to the staff team with all commands.", NamedTextColor.GREEN));
            target.sendMessage(Component.text("You have been added to the staff team! Use /modmode to get started.", NamedTextColor.GREEN));
            return true;
        }

        // /staff add <player> <command>
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[1], NamedTextColor.RED));
                return true;
            }
            if (!sm.isStaff(target.getUniqueId())) {
                sender.sendMessage(Component.text(target.getName() + " is not a staff member.", NamedTextColor.RED));
                return true;
            }
            String cmd = args[2].toLowerCase();
            if (!StaffManager.ALL_COMMANDS.contains(cmd)) {
                sender.sendMessage(Component.text("Unknown command: " + cmd + ". Valid: " + String.join(", ", StaffManager.ALL_COMMANDS), NamedTextColor.RED));
                return true;
            }
            sm.grantCommand(target.getUniqueId(), cmd);
            sender.sendMessage(Component.text("Granted /" + cmd + " to " + target.getName() + ".", NamedTextColor.GREEN));
            target.sendMessage(Component.text("You have been granted the /" + cmd + " command.", NamedTextColor.GREEN));
            return true;
        }

        // /staff remove <player> <command>
        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[1], NamedTextColor.RED));
                return true;
            }
            if (!sm.isStaff(target.getUniqueId())) {
                sender.sendMessage(Component.text(target.getName() + " is not a staff member.", NamedTextColor.RED));
                return true;
            }
            String cmd = args[2].toLowerCase();
            if (!StaffManager.ALL_COMMANDS.contains(cmd)) {
                sender.sendMessage(Component.text("Unknown command: " + cmd + ". Valid: " + String.join(", ", StaffManager.ALL_COMMANDS), NamedTextColor.RED));
                return true;
            }
            sm.revokeCommand(target.getUniqueId(), cmd);
            sender.sendMessage(Component.text("Revoked /" + cmd + " from " + target.getName() + ".", NamedTextColor.YELLOW));
            target.sendMessage(Component.text("Your access to /" + cmd + " has been revoked.", NamedTextColor.YELLOW));
            return true;
        }

        sender.sendMessage(Component.text("Usage: /staff <player> | /staff add <player> <cmd> | /staff remove <player> <cmd>", NamedTextColor.YELLOW));
        return true;
    }
}
