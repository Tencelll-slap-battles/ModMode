package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SudoCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public SudoCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "sudo")) return true;
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /sudo <player|@a|@r|@o|@p> <message>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String selector = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        List<Player> targets = SelectorUtil.resolvePlayers(selector, staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(selector), NamedTextColor.RED));
            return true;
        }

        for (Player target : targets) {
            // Use performChat to send the message as if the player typed it
            target.chat(message);
        }

        staff.sendMessage(Component.text(
                "Forced " + targets.size() + " player(s) to say: " + message, NamedTextColor.GRAY));
        return true;
    }
}
