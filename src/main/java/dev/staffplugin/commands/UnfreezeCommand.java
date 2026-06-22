package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class UnfreezeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public UnfreezeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "unfreeze")) return true;
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /unfreeze <player|@a|@o>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        List<Player> targets = SelectorUtil.resolvePlayers(args[0], staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(args[0]), NamedTextColor.RED));
            return true;
        }

        int count = 0;
        for (Player target : targets) {
            if (!plugin.getStaffManager().isFrozen(target.getUniqueId())) continue;
            plugin.getStaffManager().unfreeze(target.getUniqueId());
            target.sendMessage(Component.text("You have been unfrozen.", NamedTextColor.GREEN));
            count++;
        }

        if (count == 0) {
            staff.sendMessage(Component.text("None of the selected players were frozen.", NamedTextColor.YELLOW));
        } else {
            staff.sendMessage(Component.text("Unfroze " + count + " player(s).", NamedTextColor.GREEN));
        }
        return true;
    }
}
