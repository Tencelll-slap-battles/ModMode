package dev.staffplugin.listeners;

import dev.staffplugin.managers.StaffManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    private final StaffManager staffManager;

    public FreezeListener(StaffManager staffManager) {
        this.staffManager = staffManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!staffManager.isFrozen(player.getUniqueId())) return;

        // Allow head rotation, block body movement
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            event.setTo(event.getFrom().setDirection(event.getTo().getDirection()));
            player.sendActionBar(Component.text("You are frozen!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Unfreeze on disconnect to avoid permanent freeze state
        staffManager.unfreeze(event.getPlayer().getUniqueId());
    }
}
