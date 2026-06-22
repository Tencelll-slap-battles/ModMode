package dev.staffplugin.commands;

import dev.staffplugin.StaffPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
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
            sender.sendMessage(Component.text("Usage: /title <player> <title text>", NamedTextColor.YELLOW));
            return true;
        }

        Player staff = (Player) sender;
        String selector = args[0];
        String titleText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Component titleComponent = Component.text(titleText, NamedTextColor.WHITE);
        Title title = Title.title(titleComponent, Component.empty(),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500)));

        // Try player name first
        Player target = Bukkit.getPlayer(selector);
        if (target != null) {
            target.showTitle(title);
            staff.sendMessage(Component.text("Sent title to " + target.getName() + ".", NamedTextColor.GREEN));
            return true;
        }

        // Try entity selector
        try {
            List<org.bukkit.entity.Entity> entities = Bukkit.selectEntities(staff, selector);
            int count = 0;
            for (org.bukkit.entity.Entity e : entities) {
                if (e instanceof Player p) {
                    p.showTitle(title);
                    count++;
                }
            }
            if (count == 0) {
                staff.sendMessage(Component.text("No players matched: " + selector, NamedTextColor.RED));
            } else {
                staff.sendMessage(Component.text("Sent title to " + count + " player(s).", NamedTextColor.GREEN));
            }
        } catch (Exception e) {
            staff.sendMessage(Component.text("Invalid target: " + selector, NamedTextColor.RED));
        }

        return true;
    }
}
