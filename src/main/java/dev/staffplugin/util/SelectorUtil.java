package dev.staffplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolves custom selectors and vanilla selectors to lists of Players or Entities.
 *
 * Supported selectors:
 *   @a  - all online players
 *   @r  - one random player
 *   @s  - the sender themselves
 *   @p  - nearest player to sender
 *   @o  - all players except the sender
 *   @e  - all entities in sender's world (also supports vanilla @e[type=...] via Bukkit)
 *   anything else is treated as a player name
 */
public class SelectorUtil {

    /**
     * Resolve a selector/name to a list of Players.
     * Returns empty list if nothing matched.
     */
    public static List<Player> resolvePlayers(String selector, Player sender) {
        return switch (selector.toLowerCase()) {
            case "@a" -> new ArrayList<>(Bukkit.getOnlinePlayers());
            case "@r" -> {
                List<Player> all = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (all.isEmpty()) yield List.of();
                yield List.of(all.get(new Random().nextInt(all.size())));
            }
            case "@s" -> List.of(sender);
            case "@p" -> {
                Player nearest = null;
                double dist = Double.MAX_VALUE;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(sender)) continue;
                    if (!p.getWorld().equals(sender.getWorld())) continue;
                    double d = p.getLocation().distanceSquared(sender.getLocation());
                    if (d < dist) { dist = d; nearest = p; }
                }
                yield nearest != null ? List.of(nearest) : List.of();
            }
            case "@o" -> Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(sender))
                    .collect(Collectors.toList());
            default -> {
                // Try vanilla selector (e.g. @e[type=player])
                if (selector.startsWith("@")) {
                    try {
                        List<Entity> entities = Bukkit.selectEntities(sender, selector);
                        yield entities.stream()
                                .filter(e -> e instanceof Player)
                                .map(e -> (Player) e)
                                .collect(Collectors.toList());
                    } catch (Exception e) {
                        yield List.of();
                    }
                }
                // Plain player name
                Player p = Bukkit.getPlayer(selector);
                yield p != null ? List.of(p) : List.of();
            }
        };
    }

    /**
     * Resolve a selector to a list of Entities (for /kill etc.).
     * Falls back to player resolution for non-@e selectors.
     */
    public static List<Entity> resolveEntities(String selector, Player sender) {
        if (selector.startsWith("@")) {
            try {
                return new ArrayList<>(Bukkit.selectEntities(sender, selector));
            } catch (Exception ignored) {}
        }
        // Plain player name
        Player p = Bukkit.getPlayer(selector);
        if (p != null) return List.of(p);
        return List.of();
    }

    /**
     * Whether a selector could return more than one target.
     * Used to decide if a command should allow it.
     */
    public static boolean isMultiSelector(String selector) {
        String s = selector.toLowerCase();
        return s.equals("@a") || s.equals("@o") || s.equals("@e") || s.startsWith("@e[") || s.startsWith("@a[");
    }

    /** Returns a friendly "not found" message for a selector. */
    public static String notFound(String selector) {
        return "No players matched: " + selector;
    }
}
