package dev.staffplugin.listeners;

import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Intercepts plain /command attempts for commands that overlap with vanilla
 * (tp, kill, gamemode, give, kick, ban, title) and blocks them if the sender
 * is NOT using the /staffplugin: namespace.
 *
 * This ensures:
 *   /gamemode  → vanilla (ops only, can target others etc.)
 *   /staffplugin:gamemode → our staff version
 */
public class CommandNamespaceListener implements Listener {

    // Commands that clash with vanilla and must be namespaced to use our version
    private static final Set<String> GUARDED = Set.of(
            "tp", "kill", "gamemode", "gm", "give", "kick", "ban", "title"
    );

    private final StaffManager staffManager;

    public CommandNamespaceListener(StaffManager staffManager) {
        this.staffManager = staffManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage(); // e.g. "/gamemode creative"
        if (!msg.startsWith("/")) return;

        // Strip leading slash and grab the command label
        String[] parts = msg.substring(1).split(" ", 2);
        String label = parts[0].toLowerCase();

        // If they're already using the namespace, leave it alone
        if (label.startsWith("staffplugin:")) return;

        // If this is one of our guarded commands AND the player is staff in modmode,
        // tell them to use the namespaced version so vanilla isn't shadowed
        if (GUARDED.contains(label)) {
            Player player = event.getPlayer();
            if (staffManager.isStaff(player.getUniqueId()) && staffManager.isModModeActive(player.getUniqueId())) {
                // Don't cancel — let vanilla handle it as normal.
                // Just remind them our staff version needs the prefix.
                player.sendMessage(Component.text(
                        "Tip: Use /staffplugin:" + label + " for the staff version of this command.",
                        NamedTextColor.GRAY
                ));
                // We deliberately do NOT cancel so vanilla still works.
            }
        }
    }
}
