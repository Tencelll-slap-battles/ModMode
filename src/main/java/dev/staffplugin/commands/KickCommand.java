package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KickCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public KickCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "kick")) return true;
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /kick <player|@a|@o|@r|@p> [message]", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String selector = args[0];
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "You have been kicked.";

        List<Player> targets = SelectorUtil.resolvePlayers(selector, staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(selector), NamedTextColor.RED));
            return true;
        }

        for (Player target : targets) {
            target.kick(Component.text(reason, NamedTextColor.RED));
        }
        staff.sendMessage(Component.text("Kicked " + targets.size() + " player(s): " + reason, NamedTextColor.GREEN));
        return true;
    }
}
