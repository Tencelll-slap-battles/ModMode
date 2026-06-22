package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public TpCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "tp")) return true;

        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /tp <x> <y> <z>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            Location dest = new Location(staff.getWorld(), x, y, z,
                    staff.getLocation().getYaw(), staff.getLocation().getPitch());
            staff.teleport(dest);
            staff.sendMessage(Component.text(
                    String.format("Teleported to %.1f, %.1f, %.1f.", x, y, z), NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            staff.sendMessage(Component.text("Coordinates must be numbers.", NamedTextColor.RED));
        }

        return true;
    }
}
