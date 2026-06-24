package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvSeeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public InvSeeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "invsee")) return true;
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /invsee <player>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        if (target.equals(staff)) {
            staff.sendMessage(Component.text("You can't invsee yourself.", NamedTextColor.RED));
            return true;
        }

        // Open a copy of the target's inventory directly — staff can take/give items live
        staff.openInventory(target.getInventory());
        staff.sendMessage(Component.text("Viewing " + target.getName() + "'s inventory.", NamedTextColor.GREEN));
        return true;
    }
}
