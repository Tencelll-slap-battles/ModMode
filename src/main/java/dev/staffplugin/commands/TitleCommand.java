package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import dev.staffplugin.util.SelectorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TitleCommand implements CommandExecutor {

    private final StaffPlugin plugin;

    public TitleCommand(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkStaffCommand(plugin, sender, "title")) return true;
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /title <player|@a|@o|@r|@p> <text>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String titleText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Title title = Title.title(
                Component.text(titleText, NamedTextColor.WHITE),
                Component.empty(),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );

        List<Player> targets = SelectorUtil.resolvePlayers(args[0], staff);
        if (targets.isEmpty()) {
            staff.sendMessage(Component.text(SelectorUtil.notFound(args[0]), NamedTextColor.RED));
            return true;
        }

        for (Player target : targets) {
            target.showTitle(title);
        }
        staff.sendMessage(Component.text("Sent title to " + targets.size() + " player(s).", NamedTextColor.GREEN));
        return true;
    }
}
