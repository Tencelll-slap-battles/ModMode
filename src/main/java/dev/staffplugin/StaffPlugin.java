package dev.staffplugin;

import dev.staffplugin.commands.*;
import dev.staffplugin.listeners.CommandNamespaceListener;
import dev.staffplugin.listeners.FreezeListener;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class StaffPlugin extends JavaPlugin {

    private StaffManager staffManager;

    // Commands that clash with vanilla — we unregister the short form so vanilla takes over
    private static final List<String> VANILLA_CLASHES = List.of(
            "tp", "kill", "kick", "ban", "title", "gamemode", "give"
    );

    @Override
    public void onEnable() {
        staffManager = new StaffManager(this);

        // Admin commands
        reg("staff",    new StaffCommand(this));
        reg("unstaff",  new UnstaffCommand(this));

        // Staff commands
        reg("modmode",  new ModModeCommand(this));
        reg("to",       new ToCommand(this));
        reg("bring",    new BringCommand(this));
        reg("tp",       new TpCommand(this));
        reg("kill",     new KillCommand(this));
        reg("kick",     new KickCommand(this));
        reg("ban",      new BanCommand(this));
        reg("title",    new TitleCommand(this));
        reg("freeze",   new FreezeCommand(this));
        reg("unfreeze", new UnfreezeCommand(this));
        reg("gamemode", new GamemodeCommand(this));
        reg("invsee",   new InvSeeCommand(this));
        reg("give",     new GiveCommand(this));
        reg("sudo",     new SudoCommand(this));
        reg("glow",     new GlowCommand(this));

        // Tab completers
        StaffTabCompleter staffTab = new StaffTabCompleter(this);
        List.of("staff","unstaff","to","bring","kick","ban","title",
                "freeze","unfreeze","gamemode","invsee","give","sudo","glow")
            .forEach(cmd -> {
                PluginCommand pc = getCommand(cmd);
                if (pc != null) pc.setTabCompleter(staffTab);
            });

        // Unregister the plain /command form for anything that clashes with vanilla.
        // After this, /tp /kill /gamemode etc. will fall through to vanilla.
        // Staff must use /staffplugin:tp /staffplugin:gamemode etc.
        unregisterPlainForms();

        // Listeners
        getServer().getPluginManager().registerEvents(new FreezeListener(staffManager), this);
        getServer().getPluginManager().registerEvents(new CommandNamespaceListener(staffManager), this);

        getLogger().info("StaffPlugin enabled! Use /staffplugin:<command> for all staff commands.");
    }

    @Override
    public void onDisable() {
        staffManager.save();
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    private void reg(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) cmd.setExecutor(executor);
    }

    /**
     * Removes the plain /<command> registration from the server's command map
     * for every command that clashes with a vanilla command.
     * The /staffplugin:<command> form remains fully functional.
     */
    private void unregisterPlainForms() {
        var commandMap = Bukkit.getServer().getCommandMap();
        for (String name : VANILLA_CLASHES) {
            // The plain form is registered under the command name directly
            Command existing = commandMap.getCommand(name);
            if (existing instanceof PluginCommand pc && pc.getPlugin().equals(this)) {
                existing.unregister(commandMap);
                // Remove from the known commands map so vanilla reclaims it
                commandMap.getKnownCommands().remove(name);
                commandMap.getKnownCommands().remove("staffplugin:" + name); // re-add below
            }
            // Re-register ONLY the namespaced form
            PluginCommand pluginCmd = getCommand(name);
            if (pluginCmd != null) {
                commandMap.getKnownCommands().put("staffplugin:" + name, pluginCmd);
            }
        }

        // For non-clashing commands (staff, unstaff, modmode, to, bring, freeze,
        // unfreeze, invsee, sudo, glow) — vanilla has no equivalent so the plain
        // form is fine to keep. Players can still do /freeze, /invsee etc.
    }
}
