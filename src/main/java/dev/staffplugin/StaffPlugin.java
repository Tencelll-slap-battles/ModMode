package dev.staffplugin;

import dev.staffplugin.commands.*;
import dev.staffplugin.listeners.FreezeListener;
import dev.staffplugin.managers.StaffManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class StaffPlugin extends JavaPlugin {

    private StaffManager staffManager;

    @Override
    public void onEnable() {
        staffManager = new StaffManager(this);

        // Admin commands (no mod prefix — ops only)
        reg("staff",       new StaffCommand(this));
        reg("unstaff",     new UnstaffCommand(this));

        // Staff commands (all prefixed with mod)
        reg("modmodmode",  new ModModeCommand(this));
        reg("modto",       new ToCommand(this));
        reg("modbring",    new BringCommand(this));
        reg("modtp",       new TpCommand(this));
        reg("modkill",     new KillCommand(this));
        reg("modkick",     new KickCommand(this));
        reg("modban",      new BanCommand(this));
        reg("modtitle",    new TitleCommand(this));
        reg("modfreeze",   new FreezeCommand(this));
        reg("modunfreeze", new UnfreezeCommand(this));
        reg("modgamemode", new GamemodeCommand(this));
        reg("modinvsee",   new InvSeeCommand(this));
        reg("modgive",     new GiveCommand(this));
        reg("modsudo",     new SudoCommand(this));
        reg("modglow",     new GlowCommand(this));

        // Tab completers
        StaffTabCompleter staffTab = new StaffTabCompleter(this);
        List.of("staff","unstaff","modmodmode","modto","modbring","modtp","modkill",
                "modkick","modban","modtitle","modfreeze","modunfreeze","modgamemode",
                "modinvsee","modgive","modsudo","modglow")
            .forEach(cmd -> {
                PluginCommand pc = getCommand(cmd);
                if (pc != null) pc.setTabCompleter(staffTab);
            });

        // Listeners
        getServer().getPluginManager().registerEvents(new FreezeListener(staffManager), this);

        getLogger().info("StaffPlugin enabled!");
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
}
