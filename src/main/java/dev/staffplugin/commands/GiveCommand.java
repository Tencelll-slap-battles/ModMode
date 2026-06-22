package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public GiveCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "give")) return true;
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /give <player|@a|@r|@o|@p|@s> <item> [amount]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String selector = args[0];
        String itemName = args[1].toUpperCase();
        int amount = 1;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    staff.sendMessage(Component.text("Amount must be between 1 and 64.", NamedTextColor.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                staff.sendMessage(Component.text("Invalid amount: " + args[2], NamedTextColor.RED));
                return true;
            }
        }

        Material material = Material.matchMaterial(itemName);
        if (material == null) {
            staff.sendMessage(Component.text("Unknown item: " + args[1], NamedTextColor.RED));
            return true;
        }

        List<Player> targets = SelectorUtil.resolvePlayers(selector, staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(selector), NamedTextColor.RED));
            return true;
        }

        ItemStack item = new ItemStack(material, amount);
        for (Player target : targets) {
            target.getInventory().addItem(item.clone());
            target.sendMessage(Component.text("You received " + amount + "x " + material.name().toLowerCase().replace('_', ' ') + " from " + staff.getName() + ".", NamedTextColor.GREEN));
        }

        staff.sendMessage(Component.text(
                "Gave " + amount + "x " + material.name().toLowerCase().replace('_', ' ') + " to " + targets.size() + " player(s).",
                NamedTextColor.GREEN));
        return true;
    }
}
