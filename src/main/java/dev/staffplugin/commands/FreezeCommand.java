package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class FreezeCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public FreezeCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "freeze")) return true;
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /freeze <player|@a|@o|@r|@p>", NamedTextColor.YELLOW));
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
            if (plugin.getStaffManager().isFrozen(target.getUniqueId())) continue;
            plugin.getStaffManager().freeze(target.getUniqueId());
            target.sendMessage(Component.text("You have been frozen by a staff member.", NamedTextColor.RED));
            count++;
        }
        staff.sendMessage(Component.text("Froze " + count + " player(s).", NamedTextColor.GREEN));
        return true;
    }
}
